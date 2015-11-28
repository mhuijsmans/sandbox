#!/bin/sh

APP_NAME="junit-rpm"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/log.txt"
APP_JUNITTEST_SCRIPT="$APP_HOME/junittest.sh"

# if this is first install
if [ "$1" = 1 ] ; then
	echo "postinstall (first) `date`" >> "$APP_LOGFILE"
fi

# upgrade
if [ "$1" -ge 2 ] ; then
	echo "postinstall (upgrade) `date`" >> "$APP_LOGFILE"
fi

# root is owner and only root can write; make that everybody can write
chmod 666 "$APP_LOGFILE"

echo "running $APP_JUNITTEST_SCRIPT" >> "$APP_LOGFILE"
$APP_JUNITTEST_SCRIPT >> "$APP_LOGFILE" 2>&1
echo "started $APP_JUNITTEST_SCRIPT" >> "$APP_LOGFILE"

exit 0
