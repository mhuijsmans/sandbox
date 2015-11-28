#!/bin/sh

APP_NAME="app_with_jni"
APP_HOME="/opt/$APP_NAME"
APP_LIBDIR="$APP_HOME/lib"

cd "$APP_LIBDIR"
rm lib*.so

exit 0
