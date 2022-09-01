# pgsql linux开机自启动

```shell

cp /usr/local/pgsql/contrib/start-scripts/linux /etc/init.d/pgsql

vi /etc/init.d/pgsql

# 修改/etc/init.d/pgsql中的prefix为安装路径/usr/local/pgsql

chkconfig --add pgsql

systemctl status pgsql

```

# redis linux开机自启动

```shell
vi /usr/local/redis/redis.conf
# 将daemonize no修改为daemonize yes

cp /usr/local/redis/utils/redis_init_script /etc/init.d/redis

vi /etc/init.d/redis

# 查看REDISPORT和CONF
# 将redis.conf按要求拷贝到对应目录
cp /usr/local/redis/redis.conf /etc/redis/6379.conf

chmod +x /etc/init.d/redis

chkconfig --add /etc/init.d/redis

systemctl status redis

```

> redis 还可以使用 redis.service 配置开机自启动
