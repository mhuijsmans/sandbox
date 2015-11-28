#!/bin/sh

APP_NAME="maven-rpmtest"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/rpm-log.txt"

# if this is first install
if [ "$1" = 1 ] ; then
	echo "postinstall (first) `date`" >> "$APP_LOGFILE"
fi

# upgrade
if [ "$1" -ge 2 ] ; then
	echo "postinstall (upgrade) `date`" >> "$APP_LOGFILE"
fi

# assumption: the service-name = app-name. 
/sbin/chkconfig --add "$APP_NAME"
/sbin/chkconfig "$APP_NAME" on

# (re)start the service
/sbin/service "$APP_NAME" restart

exit 0
