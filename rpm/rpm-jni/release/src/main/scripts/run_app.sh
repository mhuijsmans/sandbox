#!/bin/sh

APP_NAME="app_with_jni"
APP_HOME="/opt/$APP_NAME"
APP_LIBDIR="$APP_HOME/lib"

cd "$APP_HOME"
java -Djava.library.path="$APP_LIBDIR" -cp "$APP_LIBDIR/*" org.mahu.rpm.rpm_jni.App

exit 0
