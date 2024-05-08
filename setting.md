## install MongoDB with wget
[bigdata@linux ~]$ wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel62-3.4.3.tgz
//decompress the file
[bigdata@linux backup]$ tar -xf mongodb-linux-x86_64-rhel62-3.4.3.tgz -C ~/
// move the decompressed file to the destination
[bigdata@linux ~]$ mv mongodb-linux-x86_64-rhel62-3.4.3/ /usr/local/mongodb
// create folder of data for log and data
[bigdata@linux mongodb]$ mkdir /usr/local/mongodb/data/
// create db folder to store data
[bigdata@linux mongodb]$ mkdir /usr/local/mongodb/data/db/
// create log file to store logs
[bigdata@linux mongodb]$ mkdir /usr/local/mongodb/data/logs/
// create log file
[bigdata@linux mongodb]$ touch /usr/local/mongodb/data/logs/ mongodb.log
// create file mongodb.conf for configuration of MongoDB
[bigdata@linux mongodb]$ touch /usr/local/mongodb/data/mongodb.conf
// 在mongodb.conf文件中输入如下内容
[bigdata@linux mongodb]$ vim ./data/mongodb.conf
#port
port = 27017
#data path
dbpath = /usr/local/mongodb/data/db
#log file
logpath = /usr/local/mongodb/data/logs/mongodb.log
#running background
fork = true
#log output 
logappend = true
#if need auth
#auth = true

after finish MongoDB installation, run the server
// run the server
[bigdata@linux mongodb]$ sudo /usr/local/mongodb/bin/mongod -config /usr/local/mongodb/data/mongodb.conf
// access the service
[bigdata@linux mongodb]$ /usr/local/mongodb/bin/mongo
// Stop the service
[bigdata@linux mongodb]$ sudo /usr/local/mongodb/bin/mongod -shutdown -config /usr/local/mongodb/data/mongodb.conf

## Redis（single node) setting
// download redis source code
[bigdata@linux ~]$wget http://download.redis.io/releases/redis-4.0.2.tar.gz 
// decompress the file
[bigdata@linux ~]$ tar -xf redis-4.0.2.tar.gz -C ~/
// cd the Redis source code and run install
[bigdata@linux ~]$ cd redis-4.0.2/
// install gcc
[bigdata@linux ~]$ sudo yum install gcc
// compile source code
[bigdata@linux redis-4.0.2]$ make MALLOC=libc
// compile install
[bigdata@linux redis-4.0.2]$ sudo make install
// create config file
[bigdata@linux redis-4.0.2]$ sudo cp ~/redis-4.0.2/redis.conf /etc/ 
// change the setting as below:
[bigdata@linux redis-4.0.2]$ sudo vim /etc/redis.conf
daemonize yes   #line 37  #if run in daemon fashion. default not run in background
pidfile /var/run/redis/redis.pid   #line 41  #redis PID file path
bind 0.0.0.0    #line 64行  # binding the master IP address default is 127.0.0.1. note: need to change if run as a server
logfile /var/log/redis/redis.log   #line 104  #define log file path. 定义log文件位置，模式log信息定向到stdout，输出到/dev/null（可选）
dir “/usr/local/rdbfile”  #line 188  #local DB path default is ./， default install path is /usr/local/bin

run Redis
// run Redis server
[bigdata@linux redis-4.0.2]$ redis-server /etc/redis.conf
// Connect Redis server
[bigdata@linux redis-4.0.2]$ redis-cli
// stop Redis server
[bigdata@linux redis-4.0.2]$ redis-cli shutdown

## Spark（single node）setting
// download Spark installaltion package with wget
[bigdata@linux ~]$ wget https://d3kbcqa49mib13.cloudfront.net/spark-2.1.1-bin-hadoop2.7.tgz 
// decompress spark files
[bigdata@linux ~]$ tar –xf spark-2.1.1-bin-hadoop2.7.tgz –C ./cluster
// cd Spark installation folder
[bigdata@linux cluster]$ cd spark-2.1.1-bin-hadoop2.7/
// copy slave setting file
[bigdata@linux spark-2.1.1-bin-hadoop2.7]$ cp ./conf/slaves.template ./conf/slaves    
// modify slave setting file
[bigdata@linux spark-2.1.1-bin-hadoop2.7]$ vim ./conf/slaves
linux  #add the master name at the end
// copy Spark-Env setting file
[bigdata@linux spark-2.1.1-bin-hadoop2.7]$ cp ./conf/spark-env.sh.template ./conf/spark-env.sh 
SPARK_MASTER_HOST=linux       #add spark master name
SPARK_MASTER_PORT=7077        #add spark master port number

run spark
// run spark cluster
[bigdata@linux spark-2.1.1-bin-hadoop2.7]$ sbin/start-all.sh
// access spark cluster with http://linux:8080
 
// shut down cluster
[bigdata@linux spark-2.1.1-bin-hadoop2.7]$ sbin/stop-all.sh

## Zookeeper（单节点）环境配置
// download zookeeper installation with wget
[bigdata@linux ~]$ wget http://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.4.10/zookeeper-3.4.10.tar.gz 
// decompress zookeeper installation files
[bigdata@linux ~]$ tar –xf zookeeper-3.4.10.tar.gz –C ./cluster
// cd zookeeper installation folder安
[bigdata@linux cluster]$ cd zookeeper-3.4.10/
// create data folder
[bigdata@linux zookeeper-3.4.10]$ mkdir data/
// copy zookeeper setting file
[bigdata@linux zookeeper-3.4.10]$ cp ./conf/zoo_sample.cfg ./conf/zoo.cfg   
// modify zookeeper setting file
[bigdata@linux zookeeper-3.4.10]$ vim conf/zoo.cfg
dataDir=/home/bigdata/cluster/zookeeper-3.4.10/data  #change data path to the created path
//run the zookeeper
[bigdata@linux zookeeper-3.4.10]$ bin/zkServer.sh start
// check Zookeeper status
[bigdata@linux zookeeper-3.4.10]$ bin/zkServer.sh status
ZooKeeper JMX enabled by default
Using config: /home/bigdata/cluster/zookeeper-3.4.10/bin/../conf/zoo.cfg
Mode: standalone
// shutdown Zookeeper server
[bigdata@linux zookeeper-3.4.10]$ bin/zkServer.sh stop

2.5 Flume-ng（single node）setting
// download with wget
[bigdata@linux ~]$ wget http://www.apache.org/dyn/closer.lua/flume/1.8.0/apache-flume-1.8.0-bin.tar.gz
// decompress zookeeper file
[bigdata@linux ~]$ tar –xf apache-flume-1.8.0-bin.tar.gz –C ./cluster
// use later when deploy
 

2.6 Kafka（single）setting
// wget download Kafka
[bigdata@linux ~]$ wget http://mirrors.tuna.tsinghua.edu.cn/apache/kafka/0.10.2.1/kafka_2.11-0.10.2.1.tgz 
// decompress file
[bigdata@linux ~]$ tar –xf kafka_2.12-0.10.2.1.tgz –C ./cluster
// cd kafka installation file
[bigdata@linux cluster]$ cd kafka_2.12-0.10.2.1/   
// modify kafka setting file
[bigdata@linux kafka_2.12-0.10.2.1]$ vim config/server.properties
host.name=linux                  #change master name
port=9092                         #change service port
zookeeper.connect=linux:2181     #zookeeper server address
// start Kafka server, need to start the zookeeper service first.
[bigdata@linux kafka_2.12-0.10.2.1]$ bin/kafka-server-start.sh -daemon ./config/server.properties
// shutdown kafka server
[bigdata@linux kafka_2.12-0.10.2.1]$ bin/kafka-server-stop.sh
// create topic
[bigdata@linux kafka_2.12-0.10.2.1]$ bin/kafka-topics.sh --create --zookeeper linux:2181 --replication-factor 1 --partitions 1 --topic recommender
// kafka-console-producer
[bigdata@linux kafka_2.12-0.10.2.1]$ bin/kafka-console-producer.sh --broker-list linux:9092 --topic recommender
// kafka-console-consumer
[bigdata@linux kafka_2.12-0.10.2.1]$ bin/kafka-console-consumer.sh --bootstrap-server linux:9092 --topic recommender
