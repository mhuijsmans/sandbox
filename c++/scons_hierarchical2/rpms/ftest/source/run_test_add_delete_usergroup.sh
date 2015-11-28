#!/bin/bash 

if [[ $(id -u) -ne 0 ]] ; then echo "Error: run as root" ; exit 1 ; fi

function GroupExists() {
	if grep -q $1 /etc/group ; then
		echo "Group exists: $1"		
  	return 1  	
	else
		echo "Group does not exist $1"
  	return 0
	fi
}

function UserExists() {
	ret=false
	getent passwd $1 >/dev/null 2>&1 && ret=true
	if $ret ; then
		echo "User exists: $1"
		return 1
	else
		echo "User does not exist: $1"
		return 0
	fi
}

function AssertGroupExists() {
	if ! GroupExists $1 ; then
  	echo "Error: group does not exist"
  	exit 1
	fi
}	

function AssertGroupDoesNotExist() {
	if GroupExists $1 ; then
  	echo "Error: group exists"
  	exit 1  	
	fi
}

########################################	

APP_NAME="ftest"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/rpm-log.txt"
RPM_TROUBLESHOOTLOG="$APP_LOGDIR/troubleshoot-log.txt"	

mkdir -p $APP_LOGDIR
touch $APP_LOGFILE
touch $RPM_TROUBLESHOOTLOG

rm -rf $APP_LOGDIR
if UserExists $APP_NAME ; then
	echo "Deleting user"
	userdel $APP_NAME
fi	
if GroupExists $APP_NAME ; then
	echo "Deleting group"
	groupdel $APP_NAME 
fi	

AssertGroupDoesNotExist $APP_NAME

# initial install
./addGroupAndUser.sh 1

AssertGroupExists $APP_NAME
