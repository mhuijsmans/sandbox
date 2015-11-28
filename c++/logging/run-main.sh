#!/bin/bash

# ref: http://ubuntuforums.org/showthread.php?t=479255
ROOT_UID=0
if [ $UID != $ROOT_UID ]; then
	echo "Usage: sudo $0";
	exit 1
fi

LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./../external/boost_1_56_0/lib/linux
export LD_LIBRARY_PATH

APPLICATION="./main"

echo ""
echo "Executing: $APPLICATION"
echo ""

$APPLICATION 
