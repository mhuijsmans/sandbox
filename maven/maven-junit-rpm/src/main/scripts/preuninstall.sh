#!/bin/sh

APP_NAME="maven-rpmtest"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/log.txt"

# if this is uninstallation as opposed to upgrade, delete the service
# also delete app / script creates file
if [ "$1" = 0 ] ; then
	echo "preuninstall (real uninstall) `date`" >> "$APP_LOGFILE"
    
    # delete the logfile
    rm "$APP_LOGFILE"
fi

if [ "$1" -ge 1 ] ; then
	echo "preuninstall (upgrade) `date`" >> "$APP_LOGFILE"
fi

exit 0
