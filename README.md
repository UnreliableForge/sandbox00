# UnreliableForge

**We forge reliable systems from unreliable worlds.**

学習用サンプルプロジェクト。

コーディングの練習ではなく、アーキテクト作成検証のためなので、本番向けを意識した構成とする。
アーキテクトの検証なので、AWS上でビルドからデプロイまでを行う。
STG環境までということで、BLUE, GREENデプロイとかやらない。
あとでやるかもしれないし、商用環境まではあまりやりたくないので、やらないかもしれない。

AWSの構成

WAF経由でCognitoの認証を行い、トークンの管理を行う場合。

```
CloudFront
  + S3 (Reactで生成したページ。認可不要)
  + ALB -> Cognito  (ALBは認証をここで行う)
    + ECS　認可情報を参照し認可を行う。処理を行い、結果を返す。帳票などのファイルダウンロードは署名付きURLを返す。
      + Lambda ECSからLambdaを直接実行する（ECSに負荷をかけたくない場合に使用する）。
      + SQS
        + Lambda(時間がかかる処理を非同期で行う)　

MySQL
DynamoDB
```

Cognitoの認証を行い、トークンの管理は自分で行う場合。

```
CloudFront
  + S3 (Reactで生成したページ。認可不要)
    + Conginto認証
  + ALB
    + ECS　
      + 認証セッションの管理
        + 認可情報を参照し認可を行う。処理を行い、結果を返す。帳票などのファイルダウンロードは署名付きURLを返す。
        + Lambda ECSからLambdaを直接実行する（ECSに負荷をかけたくない場合に使用する）。
        + SQS
          + Lambda(時間がかかる処理を非同期で行う)　

MySQL
DynamoDB
```

## WAFとCongintoで認証を行う。

**この方法は使用しない**

認証はCognitoを使う。バックエンドは認証済みのJWTのペイロード部分を受け取るだけとなる。
ECSで認可を行い、必要な処理を行う。あるいは、Lambdaを起動するか、SQSにキューを投げる。
ユーザーの区別には、Cognitoが発行するユニークな（sub）値を使用する。同じログインIDが使用されても、同じsubが発行されることはないので、別ユーザー扱いになる。
業務上はべつな値が割り振られるかもしれないが、システム上はsubを使用する。
このUIDから認可に必要な情報をテーブルなどから取得する。

バックエンドは ALB による認証プロセスを通る。このとき、ALBは以下のリクエストヘッダを付ける。

x-amzn-oidc-data（JWT のデコード済み JSON）
x-amzn-oidc-accesstoken
x-amzn-oidc-identity

Authorization リクエストヘッダは存在しない。

テスト時は、 Authorization ではなく、　x-amzn-oidc-data　をつける。これはJWTのペイロード部分をエンコードしたもの。
バックエンドの実装時は、 x-amzn-oidc-data　からペイロードを取得する。

### 認証をCognitoで行う。セッション情報を独自管理。

認証はCognitoを使用する。セッション情報は独自管理。WAFでセッション情報の管理を行わない。
x-amzn-oidc-data（JWT のデコード済み JSON）とか使わないので、前のセクションに書いてあることは大幅に修正します。

1. ログインダイアログを作成する。

CongitoとAWSのライブラリを使用し、Cogintoでの認証を行う。
ブラウザでCognitoを使った認証を行う。受け取ったIDTokenはbackendで管理する。
ブラウザでトークンを保持しない。
アクセストークンとリフレッシュトークンは使用しない。

IDとパスワードを使用した簡単なログイン画面。
受け取ったIDTokenはbackendを送信。

```
import { TextField, Button } from "@base-ui/react";

export function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    const token = await login(username, password);
    // token をバックエンドへ送って HttpOnly Cookie に保存
  };

  return (
    <div className="flex flex-col gap-4 p-6 max-w-sm mx-auto">
      <TextField value={username} onChange={e => setUsername(e.target.value)} placeholder="Username" />
      <TextField type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" />
      <Button onClick={handleLogin}>ログイン</Button>
    </div>
  );
}
```

```
import { CognitoIdentityProviderClient, InitiateAuthCommand } from "@aws-sdk/client-cognito-identity-provider";

const client = new CognitoIdentityProviderClient({ region: "ap-northeast-1" });

async function login(username: string, password: string) {
  const command = new InitiateAuthCommand({
    AuthFlow: "USER_PASSWORD_AUTH",
    ClientId: "<YOUR_APP_CLIENT_ID>",
    AuthParameters: {
      USERNAME: username,
      PASSWORD: password,
    },
  });

  const res = await client.send(command);
  return res.AuthenticationResult?.IdToken;
}
```

受け取ったIDをサーバー側へ送る。

```
await fetch("/api/session", {
  method: "POST",
  body: JSON.stringify({ idToken }),
  headers: { "Content-Type": "application/json" },
});
```

サーバー側は、受け取ったIDを検証する。
JWTによる検証を行い、検証の成功でセッションIDを生成し、DBにsubとともに保存する。

```
@RestController
public class SessionController {

    private final JwtDecoder jwtDecoder;

    public SessionController() {
        String jwkSetUri = "https://cognito-idp.ap-northeast-1.amazonaws.com/<USER_POOL_ID>/.well-known/jwks.json";
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @PostMapping("/api/session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");

        // JWT 検証
        Jwt jwt = jwtDecoder.decode(idToken);

        // ユーザー名取得
        String username = jwt.getClaimAsString("cognito:username");

        // HttpOnly Cookie をセット
        ResponseCookie cookie = ResponseCookie.from("session", idToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(3600)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("username", username));
    }
}
```

APIは、CookieからセッションIDを取得する。取得できなければ未認証エラー。
DBにセッションIDが存在しない場合、未認証エラー。

### ローカル開発環境の構成。

docker compose を使用する。
認証（のトークン管理）方式を変更したため、これは行わない。
~~nginxは backend へのリダイレクト時に ダミーの x-amzn-oidc-data を付与する~~

```
WSL
  + nginx
    + frontend(/ を host.docker.internal:5173へリダイレクト)
    + backend(/api を backend:8080 へリダイレクト)
  db
```

ローカル動作確認環境の構成

```
WSL
  + nginx(frontend/distをホスト)
    + backend(/api を backend:8080 へリダイレクト)
  db
```

## WSL環境の設定

WSL2でUbuntuを使えるようにしましょう。

wsl --installl Ubuntu

### localeの設定

language packをインストール。localeを変更。

```
sudo apt update
sudo apt install language-pack-ja locales
sudo update-locale LANG=ja_JP.UTF-8

$ locale
LANG=ja_JP.UTF-8
LANGUAGE=
LC_CTYPE="ja_JP.UTF-8"
LC_NUMERIC="ja_JP.UTF-8"
LC_TIME="ja_JP.UTF-8"
LC_COLLATE="ja_JP.UTF-8"
LC_MONETARY="ja_JP.UTF-8"
LC_MESSAGES="ja_JP.UTF-8"
LC_PAPER="ja_JP.UTF-8"
LC_NAME="ja_JP.UTF-8"
LC_ADDRESS="ja_JP.UTF-8"
LC_TELEPHONE="ja_JP.UTF-8"
LC_MEASUREMENT="ja_JP.UTF-8"
LC_IDENTIFICATION="ja_JP.UTF-8"
LC_ALL=
```

ついでにタイムゾーンも。
いつのまにか Asia/Tokyoになっていたけど、一応確認しよう。

```
sudo timedatectl set-timezone Asia/Tokyo
```

### 必要なアプリのインストール

- Docker(Windows上にDockerDesktopをインストール)
- npm, node

```
$ curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.6/install.sh | bash
$ export NVM_DIR="$HOME/.nvm"
$ [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
$ nvm --version
$ nvm install --lts
$ node --version
$ npm --version
```

- Visual Studio Code(Windowにインストール)
- Amazon Corretto 25
- その他お好みで

### APIのパス、メソッド、戻り値

メソッドはPOSTのみ使用する。
URLはリソースのロケーションを表すためにあるものだが、APIはリソースのロケーションではない。
そのため、URLはローケーションではなく、実行するAPIのクラス名、メソッド名といったイメージになる。
そのような形でHTTPを使用した場合、HTTPのルールを厳密に守る必要はなくなる。リソースの位置を表すものを、むりやりAPIとしているため、HTTPのルールを厳密に守ると無理がでできて、無駄に複雑になる。

パスパラメータと?以降のパラメータは使用しない。パラメータはすべてリクエストボディのJSON形式で渡す。

POSTではCDNのキャッシュがきかないらしい。
APIなので、キャッシュがなくても問題ない。というか、キャッシュされないほうがいい。

戻り値もすべてJSON形式とする。

HTTPの上にできるだけシンプルな形でRCP（のようなもの）を載せたような感じになっているので、そういう意味ではあまり美しいものではないと思う。
けど、使えるものはこれしかないので、しかたがない。
そのうち、このような意図を持ったプロトコルが実装されることでしょう。
SOAP?あんなものは忘れてしまいましょう。

## APIのパスと型

SpringBootでAPIを定義した場合、当然だけど、SpringBoot内ではその型定義を使用することができる。
その型情報をfrontend(TypeScript)で使うことができたらいいのでは？

SpringBootのAPIから、自動的にOpenAPIの定義書を作成することができる。
この方法だと、frontend側ではSpringBootで定義したAPIの型情報を使うことができない。
OpenAPIの定義書では、型チェックが機能しないので、わかりにくいし、エラーの原因となりうる。
JavaScriptを使っているならともかく、せっかくTypeScriptを使っているのだから、SpringBootのメソッドの型情報をTypeScriptで使えるようにしたい。また、その型情報は自動的に生成するようにしたい。

レスポンスコードについて。

| HTTP ステータス | 意味                                 | UI の挙動                      |
| --------------- | ------------------------------------ | ------------------------------ |
| 200             | 成功                                 | 通常処理                       |
| 400             | 業務エラーまたはバリデーションエラー | 画面内でエラー表示（遷移なし） |
| 401             | 未認証                               | ログインダイアログ表示         |
| 403             | 未認可                               | 400と同じ                      |
| 500             | システムエラー                       | システムエラー画面へ遷移       |

レスポンスの例。

- 正常または業務上のエラー（未認可を含む）
- その他のエラー

基本的なレスポンスボディ。レスポンスコードは200, 400, 403
バリデーションエラーの場合はレスポンスコード400を返す。

```
{
  # APIの処理結果の詳細。
  # success, info, warning, error のどれか。
  # 画面の表示のみに使用する(MUIのAlertにあるseverityプロパティのような感じ)。
  # MUIを使うわけではないので、severityの種類は増やしてもよい。tailwindcssのseverityも増やしましょう。
  "severity" : "success",
  # "画面に表示することを意図したメッセージ。必ず存在する。
  "message" : "成功しました。",
  # APIのデータ。severityがerrorでも空の data は存在する。
  "data" : {

  }
  # コンポーネントのヘルパーテキストなどに表示することを意図したメッセージ。
  # ヘルパーテキスト用のメッセージが存在しない場合、undefined.
  # fieldnameはAPIパラメータの名前と同じ(先頭の$.を省略したJSON path形式)。
  "invalid" : {
    "user.address.zipcode" : "画面表示を意図したメッセージ"
  }
}
```

予期しないエラーの場合のレスポンスボディ。レスポンスコードは500
SpringBootの共通エラーハンドラを使用し、この形式で返す。

```
{
  # APIのエラー。
  "severity" : "error",
  # 適当なハッシュ値（セッションIDと日時とか適当なものから生成すればよい）。
  # 画面表示のときに、ハッシュ値にいい感じのメッセージをつけてください。
  # 同じ値をCloudWatchLogにエラーメッセージとともに出力する。
  "message" : "E!<ハッシュ値>",
  # APIのデータ。エラーなので空。
  "data" : {

  }
  # invalidは存在しない。
}
```

未認証の場合、レスポンスコードは401.
レスポンスボディはこんな感じ。

```
{
  "severity": "error",
  "message": "認証が必要です",
  "data": {},
}
```

特別なリクエストヘッダ、レスポンスヘッダも使用しない。
認証のためにリクエストヘッダは付与しない（もともと必要ない）。
~~リクエストヘッダは認証用のものだけ。~~
レスポンスヘッダはcontent-typeのみ使用する。これはSpringBootが自動的につける（たぶん）。なので、API側はヘッダについては意識しなくてよい。

TSでのレスポンスの定義例。ジェネリクスで合成する。Javaでもだいたい同じ。

```

export type ApiBaseResponse = {
severity: "success" | "info" | "warning" | "error" | "invalidate";
message: string;
invalid?: Record<string, string>; // JSON path → メッセージ
};

```

```

export type ApiResponse<T> = ApiBaseResponse & {
data: T;
};

```

### こんな感じでできたらいいな

構想なので、具体的な方法はこれから。
やりたいことは、大まかにいうとこんな感じ。

1. @Mappingを使わずに、Controllerから@Mappingを自動生成。
2. Controllerのパラメータ、戻り値のBeanからTypeScript側で使用する型定義を自動的に生成。

APIは SpringBootの controller/** 以下に、 *Controller というクラス名で作成する。
*Controllerのクラスには @RequestMapping("/api") をつけない。
*Controllerクラスにあるメソッドには @GetMapping("/api/v1")　のようなマッピング定義を行わない。
パッケージ名とクラス名から自動的に適切なパスとなるように設定する（アノテーションをつけたほうが楽かもしれないが、パスはクラス名/メソッド名という扱いなので、決まったルールで機械的につけてしまいたい）。

パラメータの型をBeanで定義する。@NonNullなどを使用する。戻り値の型も同様。
このBeanクラスから、frontendで使用するAPI型定義ファイルを自動生成する。

自動生成した型定義はこんな感じ。

```

export type ApiMap = {
// APIのパス。Controllerからいい感じに自動生成する。
"v1/hello": {
// v1/hello のパラメータと戻り値の型を定義し、それを指定する。
// この型定義は、メソッドのパラメータと戻り値の型から自動的に生成する。
// ? とか | null もあるので、全自動とはならないかもしれない。
// Bean定義に
// @NotNull → TS では string
// @Nullable → TS では string | null
// Optional<T> → TS では T | null
//とすることである程度自動化できる。TS側はnullは使用せずにundefinedで統一。
input: any | undefined; // まだAPIがないので、anyとしている。実際にはanyは使用しない。
output: any;
};
};

```

さすがにControllerに @Mapping をつけないというのはやりすぎかもしれないが、自動的にパスマッピングを行うというはやりたい。

## テスト

### frontend単体テスト

#### ローカル

vitestを使う。詳しくはソースを参照。
テストレポートは生成しない。
必要なら作成してもいいし、nginxで見られるようにしておくのもいいかもしれない）。
gitにもあげない。

#### AWSで行う

ローカルで行っていたことをCodeBuidで行うだけ。ビルドしてvitestを実行するだけなので、たぶん簡単にできる。
テストレポートを生成する。CodeBuildでS3にアップロードする。

アップロード先の例。最新の <DATETIME> と latest は同じものをアップロードする。

S3/<ENV>/frontend/<DATETIME>/report

テストレポート -> S3/<ENV>/frontend/<DATETIME>/report
ビルド生成物 -> S3/<ENV>/frontend/<DATETIME>/dist
テストレポート(最新) -> S3/<ENV>/frontend/latest/report
ビルド生成物(最新) -> S3/<ENV>/frontend/latest/dist
CloudFront公開 -> ビルド生成物(最新)と同じ

ローカルで作成したテストレポート（HTMLで作成）は、nginxで見られるようにするのはいい考えかもしれない。
テストレポートをちょっと見せるだけなのに、いちいちファイルコピーとかやってられないでしょ。
開発用のnginxにこの設定を書いておくのはいい考えかもしれない。
nginxでfrontendをマウントして公開ディレクトリをnginx.confで指定するだけだから、まったく負担にならない。
http://<ホスト名>:<ポート番号> で見られるようにしておくだけ。

### backend単体テスト

#### ローカルで行う

#### AWS で行う

### E2Eテスト

#### ローカルで行う

Docker composeで一発実行。ただし、frontendは事前のビルドが必要。
レポートとかまだ考えてないけど、そのうちやります。

### AWSで行う

CodeBuildでも同じことを行う。
Dockerでできているので、CodeBuild上でやるのも難しくないでしょう。

```

```
