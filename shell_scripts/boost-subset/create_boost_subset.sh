#!/bin/bash

while [ 1 ]
do

	OUTPUT=$(scons 2>&1 >/dev/null | grep "fatal error:" | awk 'BEGIN {FS=" fatal error: "}{print $2}' | awk 'BEGIN {FS=": "}{print $1}')
	if [ -z "$OUTPUT" ]
	then
		echo "Completed"
		exit 0
	else
		echo "Missing: $OUTPUT"
		dir=${OUTPUT%/*}
		file=${OUTPUT##*/}
		echo "Dir: $dir"
		echo "File: $file"
		mkdir -p "./external/boost/$dir"
		cp "../../external/boost_1_56_0/$OUTPUT" "./external/boost/$OUTPUT"
	fi	

done
