#!/bin/bash

:<<!
	功能：kafka运维脚本快捷操作
	作者：zt
	时间：2020.05.14
!

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
	echo '4)  增加topic分区数'
        echo '5)  消费（监听）topic'
	echo '6)  向指定topic生产数据'
	echo '7)  查看消费者组列表'
	echo '8)  查看指定消费者组消费详情'
	echo '9)  创建topic'
	echo '10) 重置消费者组'
        echo '11) 退出'
        echo -n '请选择输入相应的序号并回车：'
        read answer
        if [[ $answer =~ $RULE ]];then
                readOption $answer
	elif [[ $answer -eq 11 ]];then
		echo '退出程序'
		exit
	else
		option
        fi
}

function readOption(){
	case $1 in
	"1"|"7")
		selectList $1
	;;
	"2")
		topicDescribe $1
	;;
	"3")
		deleteTopic $1
	;;
	"4")
		addPartition $1
	;;
	"5")
		consumeTopic $1
	;;
	"6")
		productionData $1
	;;
	"8")
		groupDescribe $1
	;;
	"9")
		addTopic $1
	;;
	"10")
		resetGroup $1
	;;
	esac
}

function selectList(){
	if [[ $1 -eq 1 ]];then
		kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper-cs:2181 --list
	else
		kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server kafka-hs:9093 --list
	fi

	option
}

function topicDescribe(){
	echo -n "输入topic名："
        read topic_name
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper-cs:2181 --describe --topic $topic_name
	option
}

function deleteTopic(){
	echo -n "输入topic名："
        read topic_name
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic $topic_name
	option
}

function addPartition(){
	echo -n "输入topic名："
        read topic_name
	echo -n "输入增加的分区数："
	read partition_num
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 -alter --topic $topic_name --partitions $partition_num
	option
}

function consumeTopic(){
	echo -n "输入topic名："
        read topic_name
	echo -n "是否从最老的数据开始消费[y/n]"
	read yes_no
	if [[ $yes_no == [y/Y] ]];then
		topic_name="$topic_name --from-beginning"
	fi
	echo $topic_name
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka-hs:9093 --topic $topic_name
	option
}

function productionData(){
	echo -n "输入topic名："
        read topic_name
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-console-producer.sh --broker-list kafka-hs:9093 --topic $topic_name
	option
}

function groupDescribe(){
	echo -n "输入groupID："
        read group_id
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server kafka-hs:9093 --describe --group $group_id
	option
}

function addTopic(){
	echo -n "输入topic名："
        read topic_name
	echo -n "输入副本数(直接回车默认为1)："
	read replication
	[ -z "$replication" ] && replication=1
	echo -n "输入分区数(直接回车默认为60)"
	read partitions
	[ -z "$partitions" ] && partitions=60
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper-cs:2181 --replication-factor $replication --partitions $partitions --topic $topic_name
	option
}

function resetGroup(){
	echo -n "输入topic名："
        read topic_name
	echo -n "输入groupID："
        read group_id
	kubectl exec -it kafka-0 -- /opt/kafka/bin/kafka-consumer-groups.sh --reset-offsets --to-latest --topic $topic_name --bootstrap-server kafka-hs:9093 --group $group_id --execute
	option
}
cover
