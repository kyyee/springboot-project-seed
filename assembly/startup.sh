#!/bin/bash

current_path=$(cd $(dirname $0);pwd)

java_home=/usr/local/jdk/
node_home=/usr/local/node/
nginx_home=/user/local/nginx/

be_path="$current_path/be"
fe_path="$current_path/fe"

be_port=8080
fe_port=3000

be_opts="-Djava.security.egd=file:/dev/.urandom -Dfile.encoding=UTF-8 -Duser.timezone=GMT+8 -XX:+HeapDumpOnOutOfMemoryError -Xms512m -Xmx512m"
fe_opts="--max-old-space-size=120"

case "$1" in
start)
  echo "--------service run beginning-------------"
  cd $be_path && pwd && $java_home/bin/java -version
  nohup /$java_home/bin/java -jar "$be_path/app.jar" $be_opts --spring.profiles.active=${2:-"pro"} 2>&1 &
  until [ -n "$(lsof -i:$be_port|grep java|grep LISTEN|awk '{print $2}')" ]; do
    sleep 2s
  done
  echo "be service start success, sps be pid is $(lsof -i:$be_port|grep java|grep LISTEN|awk '{print $2}')"

  cd $fe_path && pwd && $nginx_home/sbin/nginx -v
  if [ ${2:-"pro"} == "dev" ]; then
    mv -f $fe_path/static/config.$2.js  $fe_path/static/config.js
    nohup $nginx_home/sbin/nginx -s reload
  else
    nohup $nginx_home/sbin/nginx -s reload
  fi
  until [ -n "$(lsof -i:$fe_port|grep nginx|grep LISTEN|awk '{print $2}')" ]; do
    sleep 2s
  done
  echo "fe service start success, sps fe pid is $(lsof -i:$fe_port|grep nginx|grep LISTEN|awk '{print $2}')"
  echo "--------service running--------------"
  ;;
stop)
  echo "--------service stop beginning--------------"
  be_pid="$(lsof -i:$be_port|grep java|grep LISTEN|awk '{print $2}')"
  if [ -n "$be_pid" ]; then
      kill -9 $be_pid
      echo "be service $be_pid stop success"
  else
      echo "be service isn't running"
  fi

  fe_pid="$(lsof -i:$fe_port|grep nginx|grep LISTEN|awk '{print $2}')"
  if [ -n "$fe_pid" ]; then
      kill -9 $fe_pid
      echo "fe service $fe_pid stop success"
  else
      echo "fe service isn't running"
  fi
  echo "--------service stop success--------------"
  ;;
restart)
  $0 stop
  sleep 2
  $0 start
  echo "--------service restart success--------------"
;;
esac
exit 0
