FROM openjdk:8u102-jdk AS base
COPY . /opt/app/
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
RUN echo > /etc/apk/repositories \
    && sed -i '1i https://mirrors.aliyun.com/alpine/v3.5/community' /etc/apk/repositories \
    && sed -i '1i https://mirrors.aliyun.com/alpine/v3.5/main' /etc/apk/repositories \
    && apk update --no-cache \
    && apk add --no-cache tini curl jq ttf-dejavu \
    && rm -rf /var/lib/apk/*

FROM scratch
ENV DEFAULT_OPTS="-Djava.security.egd=file:/dev/.urandom -Dfile.encoding=UTF-8 -Duser.timezone=GMT+8 \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/${HOSTNAME} \
-XX:+UseContainerSupport -XX:NewRatio=2 -XX:MaxRAMPercentage=50.0 -XX:MinRAMPercentage=50.0 -XX:InitialRAMPercentage=20.0"
COPY --from=base . /
ENTRYPOINT [ "tini", "--","/opt/app/startup.sh"]
