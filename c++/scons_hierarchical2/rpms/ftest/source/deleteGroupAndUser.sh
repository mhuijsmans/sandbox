#!/bin/sh

APP_NAME="ftest"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/rpm-log.txt"
RPM_TROUBLESHOOTLOG="$APP_LOGDIR/troubleshoot-log.txt"

# 0 means remove last version of package
# >=1 means install first time / upgrade
if [ "$1" -ge 1 ] ; then
	echo "postuninstall (upgrade) `date`" >> "$APP_LOGFILE"
fi

# If last version removed, also delete group&user
if [ "$1" = 0 ] ; then
	NEW_GROUP="$APP_NAME"
	NEW_USER="$APP_NAME"
	
	if [ -f "$RPM_TROUBLESHOOTLOG" ]; then
		/usr/sbin/userdel "$NEW_USER"  >> "$RPM_TROUBLESHOOTLOG" || :
		/usr/sbin/groupdel "$NEW_GROUP" >> "$RPM_TROUBLESHOOTLOG" || :
	else
		/usr/sbin/userdel "$NEW_USER"  &>/dev/null
		/usr/sbin/groupdel "$NEW_GROUP" &>/dev/null
	fi
fi

exit 0