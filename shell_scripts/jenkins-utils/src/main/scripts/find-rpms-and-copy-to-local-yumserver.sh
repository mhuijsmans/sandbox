#!/bin/bash

MY_DIR=$(dirname $(readlink -f $0))
source $MY_DIR/settings.sh

if [[ ! -d "$1" ]] ; then
    echo "Error, directory $1 does not exist, aborting."
    exit 1
fi

if [[ ! -d "$LOCAL_YUM_DIR" ]] ; then
	echo "Error, directory $LOCAL_YUM_DIR does not exists"
	exit 1
fi

for rpm in `find "$1" -type f -name *.noarch.rpm`
do

  echo "Adding rpm to repo: $rpm"
  cp $rpm "$LOCAL_YUM_DIR"
  echo "Copy completed"
  
done  

exit 0