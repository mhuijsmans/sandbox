#!/bin/bash

if [[ $(id -u) -ne 0 ]] ; then echo "Error: run as root" ; exit 1 ; fi

echo "This is a script to test creation of systemd configuration"

touch /usr/lib/systemd/system/linuxservice.service
cat <<EOT > /usr/lib/systemd/system/linuxservice.service
[Unit]
Description=Linuxservice ploration
# After=syslog.target

[Service]
TimeoutStartSec=0
Type=forking
EnvironmentFile=/etc/sysconfig/linuxservice
# ExecStartPre= dome command
ExecStart=/usr/bin/linuxservice/linuxservice \$LINUXSERVICE_OPTS
Restart=on-abort

[Install]
WantedBy=multi-user.target
EOT

touch /etc/sysconfig/linuxservice
cat <<EOT > /etc/sysconfig/linuxservice
# port run use
PORT="8181"
# dir
DIR="."

# This is the actual options string passed to linuxservice.
# Change this at your own risk.
LINUXSERVICE_OPTS="\${PORT} \${DIR}"
EOT

echo "=========================================================="
cat /usr/lib/systemd/system/linuxservice.service
echo "=========================================================="
cat /etc/sysconfig/linuxservice
echo "=========================================================="
