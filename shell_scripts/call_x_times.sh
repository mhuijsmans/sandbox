#!/bin/bash

BASEDIR=$(dirname "$0")
STOPFILE="$BASEDIR/call_x_times_stop"
rm -rf "$STOPFILE" || true

COUNTER=1
while scons -j 8 focusdataprocessor_tests gtestfilter=InitialFocusPointsControllerTest.*
do
    echo "################################################################### $COUNTER "
    COUNTER=$[$COUNTER +1]
    if [ $COUNTER -eq 100000 ]; then
       exit;
    fi

    if [ -f "$file" ]
    then
        echo "$file found."
    fi

done