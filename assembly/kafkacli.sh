#!/bin/bash

:<<!
	功能：kafka运维脚本快捷操作
	作者：zt
	时间：2020.05.14
!

KAFKA_HOME=/usr/local/kafka

KAFKA_BOOTSTRAP_SERVERS=localhost:9092

ZOOKEEPER_SERVER=localhost:2181


#输入校验
RULE="^([1-9]|10)$"

function cover(){
  clear
  echo '###############################################################################'
  echo '###                                                                         ###'
  echo '###                 ██                                                      ###'
  echo '###                ░██      ██   ██  ██   ██                                ###'
  echo '###                ░██  ██ ░░██ ██  ░░██ ██   █████   █████                 ###'
  echo '###                ░██ ██   ░░███    ░░███   ██░░░██ ██░░░██                ###'
  echo '###                ░████     ░██      ░██   ░███████░███████                ###'
  echo '###                ░██░██    ██       ██    ░██░░░░ ░██░░░░                 ###'
  echo '###                ░██░░██  ██       ██     ░░██████░░██████                ###'
  echo '###                ░░  ░░  ░░       ░░       ░░░░░░  ░░░░░░                 ###'
  echo '###                                                                         ###'
  echo '###############################################################################'
  option
}

function option(){
	echo '1)  查询topic列表'
	echo '2)  查看topic详情'
	echo '3)  删除topic'
	echo '4)  修改topic分区数'
  echo '5)  消费（监听）topic'
	echo '6)  向指定topic生产数据'
	echo '7)  查看消费者组列表'
	echo '8)  查看指定消费者组消费详情'
	echo '9)  创建topic'
	echo '10) 重置消费者组'
  echo '11) 退出'
  echo -n '请选择输入相应的序号并回车：'
  read answer
  if [[ $answer =~ $RULE ]]; then
    readOption $answer
  elif [ $answer -eq 11 ]; then
		echo '退出程序'
		exit
	else
		option
  fi
}

function readOption(){
	case $1 in
	"1")
		listTopic $1
	;;
	"2")
		topicDescribe $1
	;;
	"3")
		deleteTopic $1
	;;
	"4")
		alterPartition $1
	;;
	"5")
		consumer $1
	;;
	"6")
		producer $1
	;;
	"7")
		listGroup $1
	;;
	"8")
		groupDescribe $1
	;;
	"9")
		createTopic $1
	;;
	"10")
		resetGroup $1
	;;
	esac
}

function listTopic(){
  ${KAFKA_HOME}/bin/kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --list
	option
}

function topicDescribe(){
	echo -n "输入topic名："
  read topic_name
	${KAFKA_HOME}/bin/kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --describe --topic $topic_name
	option
}

function deleteTopic(){
	echo -n "输入topic名："
  read topic_name
	${KAFKA_HOME}/bin/kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --delete --topic $topic_name
	option
}

function alterPartition(){
	echo -n "输入topic名："
  read topic_name
	echo -n "输入分区数："
	read partition_num
	${KAFKA_HOME}/bin/kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --alter --topic $topic_name --partitions $partition_num
	option
}

function consumer(){
	echo -n "输入topic名："
  read topic_name
	echo -n "是否从最老的数据开始消费[y/n]"
	read yes_no
	[[ $yes_no == [y/Y] ]] && topic_name="$topic_name --from-beginning"
	echo $topic_name
	${KAFKA_HOME}/bin/kafka-console-consumer.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --topic $topic_name
	option
}

function producer(){
	echo -n "输入topic名："
  read topic_name
	${KAFKA_HOME}/bin/kafka-console-producer.sh --broker-list ${KAFKA_BOOTSTRAP_SERVERS} --topic $topic_name
	option
}

function listGroup(){
  ${KAFKA_HOME}/bin/kafka-consumer-groups.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --list
	option
}

function groupDescribe(){
	echo -n "输入groupId："
  read group_id
	${KAFKA_HOME}/bin/kafka-consumer-groups.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --describe --group $group_id
	option
}

function createTopic(){
	echo -n "输入topic名："
  read topic_name
	echo -n "输入副本数(直接回车默认为1)："
	read replication
	[ -z "$replication" ] && replication=1
	echo -n "输入分区数(直接回车默认为3)"
	read partitions
	[ -z "$partitions" ] && partitions=3
	${KAFKA_HOME}/bin/kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --create --topic $topic_name --replication-factor $replication --partitions $partitions
	option
}

function resetGroup(){
	echo -n "输入topic名："
  read topic_name
	echo -n "输入groupId："
  read group_id
	${KAFKA_HOME}/bin/kafka-consumer-groups.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --reset-offsets --to-latest --execute --group $group_id --topic $topic_name
	option
}
cover
