# SANDBOX00 E2Eテスト

SANDBOX00をローカルでE2Eテストを行う。
最終的にはAWS上で行う。
ローカルでの環境を作成しておくことで、環境作成のための理解を深める。

- compose.yaml

docker compose up でE2Eテストを実行するためのcomposeファイル。

nginx.conf

CloudFrontとかALBのかんたんなエミュレーションをnginxで行う。その設定ファイル。

pom.xml

マイグレーションを行ってからbackend（のテスト）を起動するためのpom.xml
