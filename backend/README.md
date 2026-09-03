# SANDBOX00 BACKEND application

簡単なJSONを返すだけのSpringBootアプリ。

## 認証

Backendで行う認証処理。実際にはJWT(IDToken)の検証と認可を行う。認証はCognitoが行う。
JWTの詳細はあまり気にしない。
暗号化アルゴリズムなんて気にしなくていい。
署名、exp, iatのチェックはライブラリのほうが行う。
使うのはsubだけだし、独自でチェックする必要があるのは issとaudだけ。

issはこんな感じ。cognitoのユーザープールの URL.
https://cognito-idp.<region>.amazonaws.com/<user_pool_id>

audはこんな感じ。Cognito のクライアント ID
71ab3c4d5e6f7g8h9i0j1k2l3m

それぞれの値をパラメータストアに入れておいて、一致するかどうかを検証するだけのかんたんな作業です。
どちらも秘密情報ではありませんので、シークレットマネージャーではなく、パラメータストアでいいです。

### セッション管理の流れ。

#### セッションの開始

セッション開始API. frontendは認証後にこれを呼ぶ。
/api/v1/session (仮)

パラメータにあるIDTokenを取得。
IDTokenの検証を行う。JWTの検証を行う。iss=ユーザープールの URL, aud=クライアント ID であることを検証する

IDTokenの検証が失敗した場合、エラー。401

subでユーザーテーブルを検索する。存在しない場合、401.

HTTPセッションを無効にする。

HTTPセッションを生成する。getSession(true) で生成。
独自にIDを生成してもいいけど、有効期限の管理をHTTPセッションに任せることができるし、Spring Session JDBCとか使えるので、HTTPセッションを使用する。

CookieにHTTPセッションIDを保存する。HTTP only, SameSite, Secure を設定する（独自管理でないので、不要）。
DBのセッションテーブルにHTTPセッションIDとsubを保存する（必要ならその他の情報を保存してもよい）。

レコードに無効になったセッションが残ってしまうので、以下のような方法でレコードを削除する。
大した量ではないので、ある程度無視してもいい気がします。その場合は、有効期限カラムを入れたほうがいいでしょう。

- 定期的に削除(MySQLイベントスケジューラー)
- Spring Session JDBCで管理
- TTL機能があるDBを使う。DynamoDBなど。

#### セッションの処理

/api/* の場合。セッションがあるかどうかFilterで検証する、

HTTPセッションを取得する。getSession(false)で新しいセッションを開始しない。取得できない場合、エラー401
HTTPセッションからセッションIDを取得する。

セッションIDでセッションテーブルを検索。存在しない場合、エラー。401
テーブルのレコードからsubを取得する。

subでユーザーテーブルを検索する。存在しない場合、401.

ユーザーテーブルからユーザー情報を取得し、ContextHolderに入れる。数が少なければ、request.setAttrubute, getAttributeでもよい。
この値は、Controllerのほうで使用することができる。

##　リクエスト

### ヘッダ

特別な付与が必要なヘッダなし。

使用するメソッドは POST のみ。リクエストボディはJSON.

## レスポンス

### ヘッダ

APIがつける特別なヘッダなし。

### ステータス

基本的にはこの型。ただし、CloudFrontやCognitoが返すものはこの形式にならない。
レスポンスはすべてJSON.

| HTTP ステータス | 意味                                 | UI の挙動                      |
| --------------- | ------------------------------------ | ------------------------------ |
| 200             | 成功                                 | 通常処理                       |
| 400             | 業務エラーまたはバリデーションエラー | 画面内でエラー表示（遷移なし） |
| 401             | 未認証                               | ログインダイアログ表示         |
| 403             | 未認可                               | 400と同じ                      |
| 500             | システムエラー                       | システムエラー画面へ遷移       |

### ボディ

共通部分。

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

### 処理成功時のレスポンス。

ステータスコード 200.
\$.data はAPIにより異なる。

```
{
  "severity" : "success",
  "message" : "メッセージ",
  "data" : {
    APIによる。
  }
  "invalid" : {
  }
}
```

### 業務エラー、バリデーションエラー

レスポンスコード400
パラメータの変更などによってやり直すことにより回復可能なエラー。
severityはエラー内容により変更可能。
バリデーションエラーの場合、invalidにエラーのフィールド名とメッセージを格納する。
\$.nvalid.field[] の値は、パラメータのJSON Paht($.は省略)。

```
{
  "severity" : "error",
  "message" : "エラーメッセージ",
  "data" : {
  }
  "invalid" : {
    "fileld" : "message",
  }
}
```

### 認証エラーのレスポンス。

認証エラーの場合。Cognitoが返すエラーはこの形式にならないことに注意。
レスポンスコード401

```
{
  "severity" : "error",
  "message" : "認証できませんでした。",
  "data" : {
  }
  "invalid" : {
  }
}
```

### 認可エラーのレスポンス。

レスポンスコード 403.
メッセージは認可によっててきとうに変更しましょう。

```
{
  "severity" : "error",
  "message" : "アクセスできません。",
  "data" : {
  }
  "invalid" : {
  }
}
```

### 予期しない復旧できないエラー。ステータスコード 500

レスポンスコード500

メッセージは適当なハッシュ値（セッションIDと日時とか適当なものから生成する）。
目印に、先頭にE!とかつけておくのもいいかもしれない。
画面表示のときに、ハッシュ値にいい感じのメッセージをつけて表示すること。
同じ値をCloudWatchLogにエラーメッセージとともに出力することで検索が簡単にできるようにする。

messageとエラーハッシュを分けることを考えたけど、サーバーが返すメッセージなんてe.getMessage()の値くらいしか入れるものがないし、そんなものを表示しても意味がない。
というわけで、messageでいいかな。

```
{
  "severity" : "error",
  "message" : "E!<ハッシュ値>",
  "data" : {
  }
  "invalid" : {
  }
}
```

## DB

DBにはMyBATIS3を使用します。
DynamicSQLの使用を検討していましたが、メリットがないので、プロバイダベース方式を使用します。
自動生成されるソースは mybatls3simple なので、アノテーションベースとなります。

- 直接SQLを書いて実装する場合がほとんど。わざわざDynamicSQLに置き換えるという手間をかける意味がない。
- アノテーションベースのSQL BuilderならDynamicSQLよりは置き換えが楽。場合により、SQLをほぼそのまま使うことができる。
- DynamicSQLでは型チェックが働くというメリットもあるが、デメリットが上回る。

### 自動生成

MyBATIS generatorを使い、基本的なCRUDクラスを自動生成します。
このクラスは、
com/unreliableforge/sandbox00/backend/domain/generated
に生成されます。

```
$ ./mvnw mybatis-generator:generate
```

### 運用

自動生成されたMapperは、Serviceクラスなどから直接使用することを禁止します。
com/unreliableforge/sandbox00/backend/domain/extension/repository
にRepositoryクラスを作成し、そのクラス経由でMapperを使用すること。

DynamicSQLを使わないので、この制約もあまり意味をもたなくなったけど、この制約はあるほうがいい。

RepositoryがDBにアクセスする方法は2つあります。
ひとつは自動生成されたmapperを使う方法。
もうひとつは、Mapperを自分で実装して使う方法です。
Mapperを自分で実装する場合は、Dynamic SQLを使うメリットがあまりないように思いますので、プロバイダベースのMapperを使うのがよいでしょう。
自動生成も、SynamiCSQLではなく、mybatis3simpleに変更しました。

Mapper は DBアクセスのためのインターフェース。
Repository はドメインモデルのためのインターフェース。

となっていますが、プロバイダベースの場合はこの区別が曖昧になりがち。というか、もう同じものとしていいのではという気がします。
プロバイダベースのMapperを使う場合は、Repositoryを挟む必要はないので、Repositoryに直接Mapperを実装すること。

ただし、以下のような要件がある場合、Repositoryを挟むことで構造的メリットが得られます。
その場合でも、RepositoryからプロバイダベースのMapper（名前はRepositoryなので、わかりにくい）をAutowiredで注入して使うのがよいでしょう。

- 複数 mapper をまとめて 1 つの操作にしたい
- トランザクション境界を Repository に置きたい
- 複雑なビジネスロジックを Service に置きたくない
- 返却型を変換したい（DTO → Domain）
- キャッシュやロギングを挟みたい
- 再生成の影響をRepositoryで吸収したい

実際にはトランザクション境界はService層に置くことが多いので、Repositoryを経由するメリットは少ないかもしれない。
ビジネスロジックが複雑でも、それをRepositoryに置くのは、Repositoryの責務を超えてしまう可能性があるため、慎重に行うこと。

プロバイダベースとなったので、ほぼそのまま直接SQLを書くことができます。
DynamicSQLを使った実装よりは格段に楽でしょう。
条件分岐などがある場合でも、DynamicSQLよりはSQL Builderを使った方が楽でしょう。

残っているめんどくさい手順は、SELECT からエンティティを生成する部分だけ。

エンティティ。com/unreliableforge/sandbox00/backend/domain/extension/entity

RepositoryのDB操作メソッドの戻り値。ほとんどの場合、SELECT <この部分と同じ>

リポジトリ。com/unreliableforge/sandbox00/backend/domain/extension/repository
ここで実装するのは、以下のうちのいづれか。

- 自動生成Mapperのリポジトリ

  ```
  /**
  * generated/mapperを使うRepositoryのサンプル。
  */
  public class SampleGeneratedBaseRepository {

      @Autowired
      private UsersMapper usersMapper;

      public Optional<Users> findBySub(String sub) {

          /*
          * ここでgenerated.mapperを使用して DBアクセスを行う。
          * 複数 mapper をまとめて 1 つの操作にしたいとかあるかもしれないが、そんなことをするくらいなら、JOINでいいとか。
          *
          */

          return Optional.of(usersMapper.selectByPrimaryKey(sub));

      }
  }
  ```

- プロバイダベースのMapper実装

  ```
  /*
  * Repositiryに直接Mapperを実装してしまう場合のサンプル。
  */
  @Mapper
  public interface SampleProvidedBasedRepository {

      @SelectProvider(type = SampleProvider.class)
      public Optional<Users> validSession(String userId);

      class SampleProvider implements ProviderMethodResolver {

          public String validSession(String userId) {
              // SQLビルダーを使ってSQLを組み立てる。
              // return new SQL() {
              // {
              // SELECT("todo_id", "todo_title", "finished", "created_at");
              // FROM("todo");
              // WHERE("todo_id = #{userId}");
              // }
              // }.toString();
              // もしくは、SQLを文字列で返す。
              return "SELECT ...  WHERE id = #{userId}";
          }
      }
  }
  ```

- プロバイダベースのベースのMapper実装を使用したリポジトリ。
  リポジトリが２段階以上にならないように注意すること。
  つまり、ここで Autowired するリポジトリは、他のRepositoryをAutowiredしていないこと。
  ```
  /*
  * 他のMapperを使用し、 Repository内で処理をはさむ場合のサンプル。
  */
  @Mapper
  public class SampleRepository {

      @Autowired
      private SampleProvidedBasedRepository sampleProvidedBasedRepository;

      public Optional<Users> validSession(String userId) {
          /*
          * Repository内で直接Mapperを実装しない場合のサンプル。
          * 以下のような要件がある場合に使用する。
          * - 複数 mapper をまとめて 1 つの操作にしたい
          * - トランザクション境界を Repository に置きたい
          * - 複雑なビジネスロジックを Service に置きたくない
          * - 返却型を変換したい（DTO → Domain）
          * - キャッシュやロギングを挟みたい
          * - 再生成の影響をRepositoryで吸収したい
          */
          return sampleProvidedBasedRepository.validSession(userId);
      }
  }
  ```
