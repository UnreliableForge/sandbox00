# AWS環境用イメージ。
# 開発用イメージから作成する。アプリケーションのjarを含むイメージを作成する。
# tarとかgzipも入ってしまうし、最小のimageから作成しないので、イメージサイズが大きめになる。あまり気にしない。
# FROM イメージはAWS ECRにアップロードするので、アカウントを作ったら変更しましょう。
# AWSではcomposeファイルはありません。
FROM backend00-dev:latest 

WORKDIR /app
COPY ./backend.jar backend.jar
ENV SPRING_PROFILES_ACTIVE=AWS
# 環境変数ENVは、ECS, Lambda上で設定するので、ここでは行わない。

ENTRYPOINT ["java", "-jar", "backend.jar"]
