# SANDBOX00 BACKEND application

簡単なJSONを返すだけのSpringBootアプリ。

### 認証

ALB -> Congitoをやめた。こっちのほうが楽ではありますが、いろいろ制約があるので。

- 生成されたIDを無効にすることができない。
- 独自のログイン画面を使いたい。

特に、独自ログイン画面を使いたいという理由が大きい。
このくらいの実装なら、ALB -> Congitoでやらなくてもいいかなと思う。

認証自体はCongintoを使使用するが、認証の結果生成されたIDTokenの管理をアプリ側で行う方法。
IDTokenのみ使用し、AccessToken, RefreshTokenは使用しない。
IDTokenは期限が短いが、セッション管理自体は独自で行うため、問題ない。
ブラウザ側ではAccessToken, RefreshTokenを見ることができないため、有効期限を気にする必要がない。
ブラウザ側にトークンを保存する必要がない。APIを呼ぶときにトークンを付ける必要がない。リフレッシュトークンもなし。呼び出しはシンプル。

IDTokenには有効期限がある。期限切れのIDTokenを /api/session（仮の名前です） に渡しても無効となる。
IDTokenは、JWTの検証が行われる。偽のIDTokenが有効になることはない。

- frontend Conginto認証. IDTokenを受け取る。これを /api/session に渡す。
- backend /api/session は、IDTokenを検証。セッションIDを生成し、これとsubをDBに保存する。有効期限はcookieの有効期限と同じ。
- APIが呼ばれたら、セッションIDを取得する。これがなければ認証隅でないとする。
- セッションIDが存在する場合、セッションIDをキーにし、DBアクセス。subを取得。そこからユーザー情報を取得したり、認可情報を得る。

最後の処理は、SpringBootのFilterで行うため、Controllerで別々に実装しなくてよい。
かんたんな認可ならFilterで行うことができる。

request.getAttributeでできる。数が少ないなら、この方法が楽。
request.getAttributeでは情報が多くなるとしんどい。
その場合、SpringBootのContextHolderを使用する。UserContextHolderを作成し、Filterでセット。Controllerで取得といったことができる。
