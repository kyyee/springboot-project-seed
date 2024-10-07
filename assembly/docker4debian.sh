#!/bin/bash

: <<!
  操作系统：debian 12.6
	功能：kafka/pgsql/mysql/redis安装
	作者：kyyee
	时间：2024.10.07
!

#docker 命令不存在则安装docker
if [ -z "$(command -v docker)" ]; then
  for pkg in docker.io docker-doc docker-compose podman-docker containerd runc; do sudo apt-get remove $pkg; done
  rm -rf /var/lib/docker
  rm -rf /var/lib/containerd

  apt-get update && apt install -y ca-certificates curl

  install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
  chmod a+r /etc/apt/keyrings/docker.asc

  echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    tee /etc/apt/sources.list.d/docker.list > /dev/null && apt-get update

  apt-get update && apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

  touch /etc/docker/daemon.json
  cat >/etc/docker/daemon.json <<-EOF
{
    "registry-mirrors": [
        "https://docker.1panel.live",
        "https://qm3h3od4.mirror.aliyuncs.com"
    ]
}
EOF
  systemctl daemon-reload
  systemctl enable docker && systemctl start docker
fi

if [ -z "$(docker network ls|grep br-app)" ]; then
  docker network create br-app
fi

docker pull bitnami/zookeeper:3.8.0 && docker rm -f zookeeper && docker run -d --restart=always -p 2181:2181 --name zookeeper --network br-app -e ALLOW_ANONYMOUS_LOGIN=yes bitnami/zookeeper:3.8.0
docker pull bitnami/kafka:3.3.1 && docker rm -f kafka && docker run -d --restart=always -p 9092:9092 --name kafka --network br-app -e ALLOW_PLAINTEXT_LISTENER=yes -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092 -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092 bitnami/kafka:3.3.1
docker pull postgres:15.1 && docker rm -f postgres && docker run -d --restart=always -p 5432:5432 --name postgres --network br-app -e POSTGRES_PASSWORD=123456 -v /data/postgresql:/var/lib/postgresql/data postgres:15.1
#docker pull mysql:8.0.32 && docker rm -f mysql && docker run -d --restart=always -p 3306:3306 --name mysql --network br-app -e MYSQL_ROOT_PASSWORD=123456 -v /data/mysql:/var/lib/mysql mysql:8.0.32
docker pull mariadb:10.10.2 && docker rm -f mariadb && docker run -d --restart=always -p 3306:3306 --name mariadb --network br-app -e MARIADB_ROOT_PASSWORD=123456 -v /data/mariadb:/var/lib/mysql mariadb:10.10.2
docker pull redis:7.0.8 && docker rm -f redis && docker run -d --restart=always -p 6379:6379 --name redis --network br-app -v /data/redis:/data redis:7.0.8 --appendonly yes --requirepass "redis_123"
