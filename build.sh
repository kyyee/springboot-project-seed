#!/bin/bash

current_path=$(cd $(dirname $0);pwd)

mvn clean package -Dmaven.test.skip=true -U

ls -al $current_path/target/

cp $current_path/target/app.jar $current_path/docker/

cd $current_path/docker/ && chmod +x *.sh

image_name=springboot-project-seed-be

docker build --no-cache=true -t ${image_name}:${detail_version}T${BUILD_TIMESTAMP} .
docker save -o $WORKSPACE/$tar_name_platform-$detail_version/images/${image_name}-${detail_version}.tar.gz ${image_name}:${detail_version}T${BUILD_TIMESTAMP}

docker rmi ${image_name}:${detail_version}T${BUILD_TIMESTAMP}

