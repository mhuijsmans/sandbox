#!/bin/bash

MY_DIR=$(dirname $(readlink -f $0))
source $MY_DIR/settings.sh

if [[ ! -d "$LOCAL_YUM_DIR" ]] ; then
	echo "Error, directory  $LOCAL_YUM_DIR does not exists"
	exit 1
fi

echo "createrepo applied to $LOCAL_YUM_DIR"
createrepo --update "$LOCAL_YUM_DIR"

exit 0