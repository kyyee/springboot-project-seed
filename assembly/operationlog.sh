#!/bin/bash

: <<!
	功能：操作日志记录
	作者：kyyee
	时间：2023.01.18
!

# 添加用户操作日志
cat >/usr/local/bin/operation_record.sh <<-EOF
{
    if [ ! -d /var/log/operation/ ]; then
        mkdir -p /var/log/operation/
    fi

    operation_file="/var/log/operation/\$(date +%Y-%m-%d).operation.log"
    if [ ! -f \${operation_file} ]; then
        touch \${operation_file}
        chmod 777 \${operation_file}
    fi

    echo \$(date "+%Y-%m-%d %T ## \$(who am i | awk "{print \\\$1\" \"\\\$2\" \"\\\$5}") ## \$(pwd) ## \$(history 1 | { read x cmd; echo \$cmd; })";) >> \${operation_file}
} 2>/dev/null
EOF
chmod +x /usr/local/bin/operation_record.sh
echo 'export PROMPT_COMMAND=/usr/local/bin/operation_record.sh' >/etc/profile.d/prompt_command.sh
source /etc/profile
