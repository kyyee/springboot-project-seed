#!/bin/sh

current_path=$(cd $(dirname $0);pwd)

if [ -f /opt/config/application-pro.properties ]; then
  mkdir -p $current_path/config
  rm -f $current_path/config/application-pro.properties
  cp /opt/config/application-pro.properties $current_path/config
fi

cd /opt/app/ && exec java ${DEFAULT_OPTS} ${JAVA_OPTS} -jar /opt/app/app.jar
