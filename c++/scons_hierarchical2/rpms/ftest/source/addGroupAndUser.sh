#!/bin/sh

APP_NAME="ftest"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/rpm-log.txt"
RPM_TROUBLESHOOTLOG="$APP_LOGDIR/troubleshoot-log.txt"

echo "Command: $@ $s"

if [ -f "$RPM_TROUBLESHOOTLOG" ]; then
	echo "preinstall, `date` arg1: $1" >> "$RPM_TROUBLESHOOTLOG"
fi 

# if upgrade
if [ "$1" -ge 2 ] ; then
	echo "preinstall (upgrade) `date`" >> "$APP_LOGFILE"
fi

# if initial install
if [ "$1" = 1 ] ; then	

	NEW_GROUP="$APP_NAME"
	NEW_USER="$APP_NAME"
	NEW_USER_HOMEDIR="$APP_HOME"
	# /bin/false is a binary that immediately exits, returning false, when its called.
	# So when someone who has false as shell logs in, they're immediately logged out when false exits.
	NEW_USER_SHELL=/bin/false 
	
	if [ -f "$RPM_TROUBLESHOOTLOG" ]; then
		echo "Checking if $NEW_USER:$NEW_GROUP exists" >> "$RPM_TROUBLESHOOTLOG"
		cat /etc/group || grep "$NEW_GROUP" >> "$RPM_TROUBLESHOOTLOG"
		id -u "$NEW_USER" &>/dev/null && echo "user exists" >> "$RPM_TROUBLESHOOTLOG"
	fi 

	# add a group
	# || : is an idiom meaning to ignore the failure of what-is-before-|| which would end the entire script.
	if [ -f "$RPM_TROUBLESHOOTLOG" ]; then
		echo "Adding group $NEW_GROUP" >> "$RPM_TROUBLESHOOTLOG"
		/usr/sbin/groupadd -r "$NEW_GROUP" >> "$RPM_TROUBLESHOOTLOG"
		echo "Added group $NEW_GROUP" >> "$RPM_TROUBLESHOOTLOG"
	else
		/usr/sbin/groupadd -r "$NEW_GROUP" &>/dev/null || :
	fi

	# add a user to a group
	# -r flag implies that no home directory is created
	# see: http://linux.about.com/od/commands/l/blcmdl8_adduser.htm
	# -c The new user's password file comment field
	if [ -f "$RPM_TROUBLESHOOTLOG" ]; then
		echo "Adding user $NEW_USER" >> "$RPM_TROUBLESHOOTLOG"
		/usr/sbin/useradd -g "$NEW_GROUP" -s "$NEW_USER_SHELL" -r -c "Test building RPM" -d "$NEW_USER_HOMEDIR" "$NEW_USER" >> "$RPM_TROUBLESHOOTLOG"
		echo "Added user $NEW_USER" >> "$RPM_TROUBLESHOOTLOG"
	else
		/usr/sbin/useradd -g "$NEW_GROUP" -s "$NEW_USER_SHELL" -r -c "Test building RPM" -d "$NEW_USER_HOMEDIR" "$NEW_USER" &>/dev/null || :
	fi
	
	if [ -f "$RPM_TROUBLESHOOTLOG" ]; then
		echo "Checking if $NEW_USER:$NEW_GROUP are created" >> "$RPM_TROUBLESHOOTLOG"
		cat /etc/group || grep "$NEW_GROUP" >> "$RPM_TROUBLESHOOTLOG"
		id -u "$NEW_USER" >> "$RPM_TROUBLESHOOTLOG"
		echo "Checking completed" >> "$RPM_TROUBLESHOOTLOG"
	fi 	

fi

exit 0