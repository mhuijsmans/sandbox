#!/bin/sh

APP_NAME="junit-rpm"
APP_HOME="/opt/$APP_NAME"
APP_LOGDIR="$APP_HOME/logs"
APP_LOGFILE="$APP_LOGDIR/log.txt"
JAVA_CLASSPATH="$APP_HOME/lib/*"
JAVA_MAINCLASS="org.junit.runner.JUnitCore"
JUNIT_TESTSUITE="org.mahu.proto.junitrpm.AppTest"

echo "APP_NAME       : $APP_NAME" >> "$APP_LOGFILE"
echo "APP_HOME       : $APP_HOME" >> "$APP_LOGFILE"
echo "APP_LOGFILE    : $APP_LOGFILE" >> "$APP_LOGFILE"
echo "JAVA_CLASSPATH : $JAVA_CLASSPATH" >> "$APP_LOGFILE"
echo "JAVA_MAINCLASS : $JAVA_MAINCLASS" >> "$APP_LOGFILE"
 
# note that to build a proper command for java on linux, that "" are essential.
# next links described redirecting and appending stdout/err
# http://stackoverflow.com/questions/876239/bash-redirect-and-append-both-stdout-and-stderr
nohup java -cp "$JAVA_CLASSPATH" $JAVA_MAINCLASS $JUNIT_TESTSUITE >> "$APP_LOGFILE" 2>&1 &

exit 0
