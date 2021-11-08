#!/bin/bash
echo "cache-loader服务启动"
# 后台运行jar包任务
nohup java -Xms1024m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -jar ./access-cache-loader-v1.1.0.jar --spring.profiles.active=local > ./pid 2>&1 &
# 将启动的进程PID写入到指定的文件中,以便后续关闭服务时使用
echo $! > ./pid
