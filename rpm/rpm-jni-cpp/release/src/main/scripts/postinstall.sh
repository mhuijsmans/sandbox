#!/bin/sh

APP_NAME="app_with_jni_cpp"
APP_HOME="/opt/$APP_NAME"
APP_LIBDIR="$APP_HOME/lib"

cd "$APP_LIBDIR"
for zipfile in `ls *.nar`; do 
  echo "$zipfile"
  for solib in `unzip -Z -1 "$zipfile" | grep "^lib/.*/jni/lib.*so$"`; do
    echo "> $solib"
    unzip -j "$zipfile" "$solib"
  done
done

exit 0
