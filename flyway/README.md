# Flywayを使ってシンプルマイグレーション

## はじめに

プロジェクト立ち上げ時に、マイグレーションが必要じゃね？という話がありました。
開始時から行わなくても、ある程度開発が進んだところからマイグレーションを導入してもいいじゃん。どうせ頻繁に更新されるんだし、そのたびにマイグレーションするの？
と思いましたが、それほどこだわりもないし、長いものには巻かれてろ。ということで、開発初期からマイグレーションを導入することとなりました。

マイグレーションは、MySQL Workbenchを使用していました。
MySQL Workbench では、2つのDBを比較し、差分のSQLを出力する機能があります。
この機能があれば、問題なくマイグレーションができると思っていたのです。

しかし、そんなうまい話があるわけはなく。

- 正しいSQLを生成しない（そのまま実行するとエラーが出るので、手動で生成されたSQLを修正）。
- 実際のDBとの間でしか比較できない（定義書から一度DBを作成し、それと比較）。
- 差分作成から適用までの手順が非常に複雑。
- マイグレーション担当者（自分のことです）の負担となる。

というわけで、MySQL Workbenchを使うのはあきらめて、手動で差分を作成するといったシンプルな手順でマイグレーションができるFlywayを採用。

DBのマイグレーションがないと、UTを行うときにDB を作ることができません。
リリース時にDBに反映させるのにも必要。
そもそもどの状態が最新かがわからなかったので（そんなバカなことがあるかと思いますよね。自分もそう思います）、この機会に、マイグレーションを行った結果、作成されたDBを最新とすることにします。

## Flyway とは

DBのマイグレーションツール。

Flywayの公式ドキュメント https://documentation.red-gate.com/flyway/reference/usage/flyway-open-source

最初の状態（ここでは、既存のSTG環境のDB）があり、その差分を積み重ねていく方式。
差分を積み上げていく方式であるため、最新のものはきれいな（ほぼCREATE TABLEで構成されている）DDLになりません。

初回の実行は、すべてのSQLが数字順に実行されます。
二回目以降は、前回実行されたものの次から実行されます。どこまで実行したかは、DBに Flyway用のテーブル flyway_schema_history が作成され、そこに記録されています。

```bash
migration/
  V1_init.sql
  V2_nantoka_modify.sql
  V3_kantoka_modify.sql
```

V1はおおもとなので、ほぼCRETAE TABLEのみとなります。

V2以降は、V1からの差分を記述します。たとえば、テーブルの追加があれば、CREATE TABLE, カラムの追加があれば、MODIFY TABLE ADD COLUMNなどのSQL文を記述します。

## Flywayを使うための準備

1. Flywayのインストール
   Docker Containerを使用するので、ローカルにFlywayをインストールする必要はありません。
   Docket Containerでflywayがつかえるかどうか試しておきましょう。

   ```bash
   $ docker pull flyway/flyway
   ```

## Flywayの運用

マイグレーション用のソースはこのREADME.mdがある場所。

### リポジトリDBのディレクトリ構成。

```bash
リポジトリsandbox
  + flyway              # Flywayのプロジェクトルート
	+ ./mvn             # maven wrapper
  + pom.xml             # 他のプロジェクトでこのSQLを使うため、mavenを使用
  + src/main/resources/db
    + migrations        # V<数値>__ で始まるマイグレーションファイルを置く場所。
      + V1__init.sql    # 最初のバージョン（現在のSTG環境を作成するSQL）。
    + repeatables        # R<数値>__ で始まるマイグレーションファイルを置く場所。
  + target              # ビルド成果物
```

マイグレーションファイルをクラスパスで参照できるようにするため、mavenを使用します。

mvn install で ローカルリポジトリにインストール。プロジェクトのpom.xml に 依存関係を追加することで、クラスパス経由でマイグレーションファイルを参照することができます。

テスト時にはクラスパス経由でマイグレーションファイルの位置を指定すること。

クラスパス系経由でアクセスする場合、変更後に mvn install でローカルリポジトリにインストールする必要があります。依存関係にこれを追加すること。

pom.xmlにあるgroupIdを設定する。ここに書いてあるものはメンテナンスされていない可能性があるので、pom.xmlを参照して正しい値を設定しましょう。

```xml
<dependency>
  <groupId>com.unreliableforge.sandbox00</groupId>
  <artifactId>migration</artifactId>
  <version>0.0.1</version>
  <scope>test</scope>
</dependency>

```

### DBのドキュメント

DBの設計書はありません。必要であれば実際に作成したDBからMySQL Workbenchや A5:SQL などを使用し、作成すること（developまたはmainリリース時に作成し、マイグレーションを行ったファイルの日時をつけておくなどのルール決めをしておいたほうがいいかもしれません。テキストファイルであれば、履歴はgitで残せるため、ファイル名にDEV, STG(PROD)をファイル名に含めるだけで充分だと思います）。

A5:SQL ではいい感じのテーブル定義書をHTMLで作成してくれます。テキストファイルだし、これでいいのでは？ ER図も出せますが、gitで管理しずらいし、あまり意味がないかも。

## マイグレーション手順

1. 空のファイルを作成する。

   日付の後ろは「\_\_」（アンダーバー２つ）。

   「適当な名前」の部分は、実際の処理に対応した名前であること。snake case(sample_nameのような形式)とする。

```bash
$ cd src/main/resources/db/migration
$ touch V$(date +%Y%m%d%H%M%S)__<適当な名前>.sql
```

1. 作成した空のファイルにマイグレーションを行うSQLを書く。
   - ここで書くべきもの
     - 前のバージョンから追加になったテーブルのCREATE TABLE 文。
     - 前のバージョンから変更になったテーブル、カラムの ALTER TABLETE 文。
     - 今回のバージョンで不要になったテーブルの DROP TABLE 文。
     - 今回のバージョンで不要になったカラムの DROP COLUMN 文。
     - 今回のバージョンで追加、変更になった INDEX, FK の変更。
   - 注意
     - MODIFY COLUMN, CHANGE COLUMN を使った場合、再指定しないとコメントが消えることがあります。これを使う場合、コメントは必ず指定してください。
     - CREATE TABLE の前に DROP TABLE は書かないでください。
     - CREATE TABLE や ALTER TABLE を行う場合、IF NOT EXISTS は書かないでください。
     - 明示的にテーブルを削除する場合のみ DROP TABLEを使うこと。この場合、IF EXISTS は不要です。
     - CREATE TABLEを使う場合は、 ENGINE, CHARSET, COLLATEを指定すること（COLLATEは用途によって別なものを指定することも可）。

```sql
// ENGINE, CHARSET, COLLATEの設定例
CREATE TABLE `SAMPLE` (
  `COLUMN1` varchar(10) NOT NULL DEFAULT '' COMMENT 'サンプルカラム1',
  PRIMARY KEY (`COLUMN1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='サンプルテーブル1'; # ここ！
```

1. マイグレーションを行ってみる。

   「マイグレーションを実行 」のところにあるコマンドでマイグレーションを行う。

   確認する場合、ローカルのMySQLに新しいスキーマを作成し、それに対してマイグレーションを行うことをおすすめします（問題が起こったら、DROP DATABASE で削除してからやりなおし）。
   - 実行時にエラーとならないか。
   - 正しく反映されているか（実際のDBを確認）。

2. インストール

   テストで使用する場合、マイグレーションファイルをMavenローカルリポジトリへインストール。

   ```bash
   # リポジトリ db のルートから。
   $ ./mvnw install
   ```

3. リリース

   P_warrantyやP_warraty_apiをdevelopやmainにマージするとき、同時に db のブランチのマージも行う。

   STGへリリースする場合、developへマージ。Prodへリリースする場合、mainへマージする。

   次回のビルドで自動的に適用される（ようにはなっていないので、引継ぎが完全に終了したら、この設定を行うこと）。

4. Prodへリリース

   Prodでリリースする場合も Stg と同じです。接続先を Prod にして flyway migrate を実行するだけ（ProdはSSMポートフォワードを使ってないと思うので、同じ方法で接続することはできないと思いますが、なんとかして接続してください）。

   **StgとProdでDBの構成が異なっていると、マイグレーションに失敗する可能性があります。StgとProdのDBは同じになっていなければならないので、ここでエラーが起こることはないはず。**

   flywayを使うことができない場合、flywayのマイグレーションファイルを手動で実行しても同じようになるはずですが、STGと同じ構成とならない可能性があるため、おすすめしません。

## マスタデータの管理

必要であれば、マスタデータもFlyway で管理できます（V1では作成していません）。Flywayで管理するのは。不変のマスタデータのみです。それ以外のデータをここで追加しないこと。

Rで始まるファイルのほうで テーブル作成やテーブル、カラム定義の変更を行わないこと。

以下のファイルにINSERT を記述する。

数値の部分は、現在の日時（YYYYMMDDHHMMSS）

```bash
$ cd repeatable
$ touch R$(date +%Y%m%d%H%M%S)__<適当な名前>.sql/
```

空のファイルが作成されるので、Vのほうと同じようにINSERT(UPSERT)などを書く。

V で始まるファイル(migrationディレクトリにあるファイル)が実行されたあと、repeatable にある Rで始まるファイルが順に実行されます。前回と変更がない場合、実行されません（Flywayはファイルのハッシュコードで変更を認識しているらしい）。

データ追加時に、単純にINSERTだけを書くと、二回目以降にエラーになります。変更した場合は、変更があったSQLファイルを最初から実行するようになっているので、二回目以降もエラーにならないようにする必要があります。

方法1. 一度DELETEしてから INSERT

この方法は、FK違反が起きる可能性があります。以下の方法でFK違反を一時的に無視することができます。

```bash
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM status;
SET FOREIGN_KEY_CHECKS = 1;
```

方法2. いわゆる UPSERT というやつ。通常はこちらで問題ないと思います。

```bash
INSERT INTO status (id, name) VALUES
  (1, 'ACTIVE'),
  (2, 'INACTIVE')
ON DUPLICATE KEY UPDATE
name = VALUES(name);
```

ビューやストアドプロシージャなどがあれば、それも R のほうで作成してください。

繰り返し実行されてもエラーとならないようにすること。

```
CREATE OR REPLACE VIEW user_view AS
SELECT id, name FROM user;
```

Viewであれば、CREATEだけでなく、このようにCREATE REPLACE を使使用する。

## マイグレーションを実行

コマンドが長いので、スクリプトにしたりすることをおすすめします（いちいちここからコピペとかしないように）。

このコマンドでマイグレーションを行います。-url パラメータでマイグレーションを行うDBを指定します。

### マイグレーションを行う場合のコマンド例

コメント付きなので、実際に使用する際にはコメントを削除してください。
DBはDocker Containerとなっています。
環境によって、ソースのマウントディレクトリやlocationなどを変更すること。

-v ${pwd}は、 src/main/resources で実行する場合の例です。実行する場所により変更すること。

```bash
# Docker Containerで 動作しているMySQLに対してマイグレーションを行う場合のコマンド例。
$ docker run --rm \
  --network pwt-net \               # MySQL Docker Container が動作しているネットワーク
  -v $(pwd):/flyway/sql \ # /migrate, repeatableディレクトリがあるディレクトリを/flyway/sqlマウント
  flyway/flyway \                   # flyway docker image
  -url=jdbc:mysql://pwtdb:3306/P_warranty_migrate_test0?useSSL=false # マイグレーションを行うDB
  -user=root \                      # マイグレーションを行うDBのユーザー名
  -password=root \                  # マイグレーションを行うDBのパスワード
  -locations=filesystem:/flyway/sql/migrations,filesystem:/flyway/sql/repeatables \ # マイグレーションファイルがある場所
  migrate                           # migrate コマンド
```

実際に使用するときには、compose.yaml で行うといいと思います。
docker compose run --rm flyway <command> で実行可能。

```yaml
# docker compose run --rm flyway <command>
name: sandbox00

services:
  flyway:
    # 公式のものがWAS Punlic ECRにない。AWS privare ECR にアップロードし、それを使用するようにすること。
    image: flyway/flyway
    container_name: flyway_migration
    networks:
      - sandbox00
    volumes:
      - ./src/main/resources/db:/flyway/sql
    environment:
      - FLYWAY_URL=jdbc:mysql://db:3306/sandbox00?useSSL=false&allowPublicKeyRetrieval=true
      - FLYWAY_USER=sandbox
      - FLYWAY_PASSWORD=sandbox
      - FLYWAY_LOCATIONS=filesystem:/flyway/sql/migrations,filesystem:/flyway/sql/repeatables

networks:
  sandbox00:
    external: true
    name: sandbox00
```

参考までにflywayの実行結果（integer型を使用しているので警告が多数）

```
Flyway OSS Edition 12.8.1 by Redgate

See release notes here: https://help.red-gate.com/help/flyway-cli12/help_8.aspx?topic=release-notes-and-older-versions/release-notes-for-flyway-engine
Database: jdbc:mysql://pwdev_db:3306/migration?useSSL=false (MySQL 8.0)
1 SQL migrations were detected but not run because they did not follow the filename convention.
Set 'validateMigrationNaming' to true to fail fast and see a list of the invalid file names.
Schema history table `migration`.`flyway_schema_history` does not exist yet
Successfully validated 4 migrations (execution time 00:00.029s)
Creating Schema History table `migration`.`flyway_schema_history` ...
Current version of schema `migration`: << Empty Schema >>
Migrating schema `migration` to version "1 - init"
Migrating schema `migration` to version "20260610174214 - alter twar"
Migrating schema `migration` to version "20260610174245 - create tsmr"
WARNING: DB: Integer display width is deprecated and will be removed in a future release. (SQL State: - Error Code: 1681)
WARNING: DB: Integer display width is deprecated and will be removed in a future release. (SQL State: - Error Code: 1681)
WARNING: DB: Integer display width is deprecated and will be removed in a future release. (SQL State: - Error Code: 1681)
WARNING: DB: Integer display width is deprecated and will be removed in a future release. (SQL State: - Error Code: 1681)
Migrating schema `migration` to version "20260610174450 - create tcid"
Successfully applied 4 migrations to schema `migration`, now at version v20260610174450 (execution time 00:00.667s)

```

### AWS RDS 上のMySQLに対してマイグレーションを行う場合のコマンド例。

あらかじめ ssm ポートフォワードを行っておくこと。
ポートフォワードを行った場合、DBの接続先は 127.0.0.1:<ポートフォワードのポート番号> となります（localhostではありません）。
ヒストリーにパスワードを残さないため、 read -s -p を使っています。
ローカルにパスワードを残さないのが原則であるため（ローカルファイルにパスワードをメモしておくのも推奨されません）、IAM認証にしておくことを推奨します、

```bash
$ read -s -p "Password: " DB_PASS
$ FLYWAY_URL=jdbc:mysql://localhost:13306/<DB>?useSSL=false&allowPublicKeyRetrieval=true \
  FLYWAY_USER=<USERNAME> \
  FLYWAY_PASSWORD=${DB_PASS} \
  FLYWAY_LOCATIONS=filesystem:/flyway/sql/migrations,filesystem:/flyway/sql/repeatables \
  docker compose
```

STG環境で初回のマイグレーションの場合、Flywayの履歴がないので、V1 から実行しようとしてエラーになります。
オプションでマイグレーションを開始するバージョン番号を指定すること。

```
FLYWAY_BASELINE_VERSION=<バージョン>
FLYWAY_BASELINE_ON_MIGRATE=true
```

## 参考資料

### V1ファイルを作成する方法

既存のDBから最初のSQLを作成する方法。

よけいなテーブルがあるので、作成するテーブルのみ抽出（条件 \_ を含まない）。

```bash
$ mysql \
-h 127.0.0.1 \
-P 13306 \
-u <ユーザー名> \
-p -N -e \
"
SELECT table_name
FROM information_schema.tables
WHERE table_schema='スキーマ名'
AND table_name NOT LIKE '%\\_%';
" > tables.txt
```

DUMPしてCREATE TABLEとか作る。前のステップで作成したテーブルを指定する。

```
$ mysqldump \
  -h 127.0.0.1 \
  -P 13306 \
  -u <ユーザー名> \
  -p \
  --no-data \
  --routines \
  --triggers \
  --skip-comments \
  --single-transaction \
  --set-gtid-purged=OFF \
  --no-tablespaces \
  <スキーマ名> > raw.sql
```

生成したファイルからいらないものを消す（もともと存在しないかも）。

```
DEFINER=`user`@`%`
AUTO_INCREMENT=
SET @OLD_CHARACTER_SET_CLIENT=...
LOCK TABLES ...
DROP TABLE IF EXISTS
```

CREATE TABLE にこれを追加（すでに存在するはずだけど、一応確認）。

```
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

マイグレーションを実行。正常終了し、正しくDBが作成されたことを確認すること（確認はローカルにある適当な MySQL Docker Containerで行うこと）。

### リカバリーの方法

マイグレーション適用時にエラーになった時の修復方法。

途中まで行われてしまったマイグレーションを手動でロールバック（オープンソース版では自動でロールバックする機能は利用できないらしい）。

repairで直前のmigrateを履歴から消す。その後、migrateを再実行。

```
$  docker compose run --rm flyway repair
$  docker compose run --rm flyway migrate
```

データを消してもいい場合、わざわざこんなことをせず、DROP DATABASEを行い、最初から実行したがほうがいいと思います。
