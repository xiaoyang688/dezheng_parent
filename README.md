## 1.代码结构
```
├── dezheng_api					#存放所有接口
│   ├── pom.xml
│   └── src
├── dezheng_common				#公共模块
│   ├── pom.xml
│   └── src
├── dezheng_common_service	                #公共服务模块
│   ├── pom.xml
│   └── src
├── dezheng_common_web				#公共Web模块
│   ├── pom.xml
│   └── src
├── dezheng_parent.iml
├── dezheng_pojo
│   ├── pom.xml
│   └── src
├── dezheng_service_business		        #广告服务
│   ├── pom.xml
│   └── src
├── dezheng_service_goods		        #商品服务
│   ├── pom.xml
│   └── src
├── dezheng_service_index			#更新索引服务
│   ├── pom.xml
│   └── src
├── dezheng_service_order		        #订单服务
│   ├── pom.xml
│   └── src
├── dezheng_service_sms			        #短信服务
│   ├── pom.xml
│   └── src
├── dezheng_service_tulingBot			#智能对话服务
│   ├── pom.xml
│   └── src
├── dezheng_service_user			#用户服务
│   ├── pom.xml
│   └── src
├── dezheng_web_front			        #前台服务
│   ├── pom.xml
│   └── src
├── dezheng_web_manager			        #后台管理
│   ├── pom.xml
│   └── src
├── pom.xml
├── README.md
└── sql
    └── BackUpSql.sql				#数据库文件
```
## 2.安装服务软件

#### 2.1 redis 安装
```bash
$ wget http://download.redis.io/releases/redis-5.0.7.tar.gz
$ tar xzf redis-5.0.7.tar.gz
$ cd redis-5.0.7
$ make
```
* 编译成功后，进入src文件夹，执行make install进行Redis安装。
* 创建etc和bin文件夹,将配置文件放进etc，启动文件放入bin目录里,方便管理
```bash
mv mkreleasehdr.sh redis-benchmark redis-check-aof redis-check-rdb redis-cli redis-server /usr/local/redis/bin/
```
* 首先编辑conf文件，将daemonize属性改为yes（表明需要在后台运行）
* bind 127.0.0.1 注释然外网访问和把protected-mode改为no

#### 2.2 zookeeper安装

```bash
wget http://mirrors.hust.edu.cn/apache/zookeeper/zookeeper-3.4.14/zookeeper-3.4.14.tar.gz
```
* 进入配置文件夹，复制zoo_simple.cfg并改名为zoo.cfg

#### 2.3 mysql安装
* 百度云有得下
* 下载后解压到/usr/local
* 创建数据仓库目录
```bash
mkdir /data/mysql
```
* 创建一个mysql用户，mysql用户组，并禁止mysql用户登录
```bash
groupadd mysql
useradd -r -s /sbin/nologin -g mysql mysql -d /usr/local/mysql 
```
* 更改mysql目录属有者
```bash
cd /usr/local/mysql
chown -R mysql .
chgrp -R mysql .
chown -R mysql /data/mysql
```
* 配置my.cnf
```bash
vim /etc/my.cnf
```
* 添加以下内容
```bash
[mysqld]
bind-address=0.0.0.0
port=3306
user=mysql
basedir=/usr/local/mysql
datadir=/data/mysql
socket=/tmp/mysql.sock
log-error=/data/mysql/mysql.err
pid-file=/data/mysql/mysql.pid
#character config
character_set_server=utf8mb4
symbolic-links=0
explicit_defaults_for_timestamp=true
```
* 初始化mysql
```bash
cd /usr/local/mysql/bin
./mysqld --defaults-file=/etc/my.cnf --basedir=/usr/local/mysql/ --datadir=/data/mysql/ --user=mysql --initialize #初始化
cat /data/mysql/mysql.err #查看密码
```
* 启动mysql
```bash
service mysql start
```

* 修改密码
```bash
SET PASSWORD = PASSWORD('123456');
ALTER USER 'root'@'localhost' PASSWORD EXPIRE NEVER;
FLUSH PRIVILEGES;   
```

* 远程登录
```bash
use mysql                                            #访问mysql库
update user set host = '%' where user = 'root';      #使root能再任何host访问
FLUSH PRIVILEGES;                                    
```

#### 2.4 elasticsearch安装
* 不能root用户运行，必须使用其他用户
* 出现这个问题：max file descriptors [4096] for elasticsearch process is too low
```bash
vim /etc/security/limits.conf
```
* 增加以下内容
```bash
*       soft nofile     65536
*       hard nofile     65536
```
* 出现这个问题： max virtual memory areas vm.max_map_count [65530] is too low
```bash
vi /etc/sysctl.conf
```
###### 添加以下内容
```bash
vm.max_map_count=655360
```
###### 查看是否成功
```bash
sysctl -p
```

#### 2.5 RabbitMQ中间件安装

###### 安装erlang
```bash
yum install -y erlang
```
###### 安装rabbitmq-server
```bash
wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.6/rabbitmq-server-3.6.6-1.el7.noarch.rpm
yum install rabbitmq-server-3.6.6-1.el7.noarch.rpm 
```

###### 开启外部访问
```bash
cd /etc/rabbitmq/
vim rabbitmq.config
//写入内容
[{rabbit, [{loopback_users, []}]}].
```

###### 开启rabbitmq插件管理
```bash
rabbitmq-plugins enable rabbitmq_management
```

###### 开启RabbitMQ WEB STOMP 
> 浏览器访问http://ip:15670 查看stomp例子
```bash
rabbitmq-plugins enable rabbitmq_web_stomp rabbitmq_web_stomp_examples #启动完需要重启RabbitMQ服务
```

###### 开启服务
```bash
service rabbitmq-server start
chkconfig rabbitmq-server on
```

###### 启动或停止rabbitmq服务、查看状态命令
```bash
service rabbitmq-server start
service rabbitmq-server stop
service rabbitmq-server status
```

## 3.部署方法
* ##### 1.导入sql文件到数据库，并在每个service子模块中的database.properties配置数据库连接信息
* ##### 2.修改dezheng_common模块中的zookeeper.properties修改为当前已部署的ip和端口
* ##### 3.修改dezheng_common_service模块中的elasticseach.properties、redis.properties修改为当前已部署的ip和端口

## 5.项目启动
* ##### 方式一：将所有dezheng_service_XXXXX 通过 mvn install 命令打包 然后放入tomcat中webapp 启动tomcat
* ##### 方式二：在IntelliJ IDEA右侧工具栏中找到Maven,比如点击子模块dezheng_service_goods ==》Plugins ==》tomcat7 ==》tomcat7:run
## 6.项目运行
* ##### http://localhost:9102