#!/usr/bin/env groovy

pipeline {
    agent {
        docker {
            image 'ip:port/groupname/jenkins-build-runtime-centos:20220831'
        }
    }

    environment {
        tar_name = 'sps'
        detail_version = 'V100R001B01D001SP01'
        external_version = '1.0.0'
        external_version_sub = '1.0.0'
        info = '1.springboot 种子项目'
    }
    stages {
        stage('pre-build') {
            steps {
                sh '''
                    mkdir $tar_name-$detail_version
                    mkdir -p $tar_name_vx-$detail_version/fe
                    mkdir -p $tar_name_vx-$detail_version/be
                    mkdir -p $tar_name_vx-$detail_version/be/config

                    mv $WORKSPACE/assembly/* $tar_name_vx-$detail_version/ && chmod +x $tar_name_vx-$detail_version/*.sh
                    echo "" > $tar_name-$detail_version/version.json
                    echo "{" >> $tar_name-$detail_version/version.json
                    echo "\\"app_name\\":\\"$tar_name\\"," >> $tar_name-$detail_version/version.json
                    echo "\\"internal_version\\":\\"$detail_version\\"," >> $tar_name-$detail_version/version.json
                    echo "\\"external_version\\":\\"$external_version\\"," >> $tar_name-$detail_version/version.json
                    echo "\\"info\\":[\\"内部版本号：$detail_version\\",\\"$info\\"]" >> $tar_name-$detail_version/version.json
                    echo "}" >> $tar_name-$detail_version/version.json

                    echo "app_name=$tar_name" >> $tar_name-$detail_version/version.txt
                    echo "internal_version=$detail_version" >> $tar_name-$detail_version/version.txt
                    echo "external_version=$external_version" >> $tar_name-$detail_version/version.txt

                    echo "branch=$(cat $WORKSPACE/.git/HEAD)" >> $tar_name-$detail_version/git.version
                    echo "commit_id=$(cat $WORKSPACE/.git/FETCH_HEAD)" >> $tar_name-$detail_version/git.version
                '''
            }
        }
        stage('build') {
            steps {
                sh '''
                    if [ -d $WORKSPACE/ ]; then
                        cd $WORKSPACE/
                        pwd && ls
                        chmod +x ./build.sh && bash -x ./build.sh
                    fi
                '''
            }
        }
        stage("tar") {
            steps {
                sh '''
                    tar cvzf $tar_name-$detail_version-${BUILD_TIMESTAMP}.tar.gz $tar_name-$detail_version/
                    chmod 777 $tar_name-$detail_version-${BUILD_TIMESTAMP}.tar.gz
                    md5sum $tar_name-$detail_version-${BUILD_TIMESTAMP}.tar.gz >> $tar_name-$detail_version-${BUILD_TIMESTAMP}.md5
                '''
            }
        }
        stage('ftp') {
            steps {
                ftpPublisher alwaysPublishFromMaster: false,
                        masterNodeName: "ftpname",
                        paramPublish: [parameterName: ""],
                        continueOnError: false,
                        failOnError: false,
                        publishers: [[configName             : "ftpname",
                                      transfers              : [[asciiMode         : false,
                                                                 cleanRemote       : false,
                                                                 excludes          : "",
                                                                 flatten           : false,
                                                                 makeEmptyDirs     : false,
                                                                 noDefaultExcludes : false,
                                                                 patternSeparator  : "[, ]+",
                                                                 remoteDirectory   : "/${tar_name}/${detail_version}",
                                                                 remoteDirectorySDF: false,
                                                                 removePrefix      : "",
                                                                 sourceFiles       : "*.tar.gz,*.md5"]],
                                      usePromotionTimestamp  : false,
                                      useWorkspaceInPromotion: false,
                                      verbose                : false]]
                echo "ftp://username:password@localhost/DEVELOP/GROUPNAME/${tar_name}/${detail_version}/${tar_name}-${detail_version}-${BUILD_TIMESTAMP}.tar.gz"

            }
        }
        stage('clean') {
            steps {
                sh '''
                    rm -rf $WORKSPACE/* && ls
                '''
            }
        }
    }
}
