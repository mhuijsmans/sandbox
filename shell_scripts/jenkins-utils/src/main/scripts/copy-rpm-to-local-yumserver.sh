#!/bin/bash

MY_DIR=$(dirname $(readlink -f $0))
source $MY_DIR/settings.sh

if [[ ! -f "$1" ]] ; then
    echo "File $1 does not exist, aborting."
    exit 1
fi

if [[ ! -d "$LOCAL_YUM_DIR" ]] ; then
	echo "Error: Directory  $LOCAL_YUM_DIR does not exists"
	exit 1
fi

echo "Copy rpm to repo: $1"
cp $1 "$LOCAL_YUM_DIR"
echo "Copy completed"

exit 0