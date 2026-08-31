# 開発用イメージ。
FROM public.ecr.aws/amazonlinux/amazonlinux:2023
RUN dnf install -y java-25-amazon-corretto-headless
RUN dnf install -y tar
RUN dnf install -y gzip

WORKDIR /app
#ENTRYPOINTがなくても開発中は問題ないので、ENTRYPOINTは書かない。
#ENTRYPOINT ["/bin/sh", "-c"]
