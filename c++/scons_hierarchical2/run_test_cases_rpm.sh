#!/bin/bash 

# ref: http://stackoverflow.com/questions/18215973/how-to-check-if-running-as-root-in-a-bash-script
if [[ $(id -u) -ne 0 ]] ; then echo "Error: run as root" ; exit 1 ; fi

# important: DO NOT use spaces when assigning
# so OUTPUT= S...
# doesn't work.
function callScons {
	COMMAND="scons $* 2>&1 | grep -i 'error'"
	echo "Calling command: $COMMAND"
	OUTPUT=$(eval $COMMAND)
	if [ -n "$OUTPUT" ]; then 
		echo "ERROR: $OUTPUT"
		exit 0 
	fi	
}

function callYum {
	COMMAND="yum -y $* 2>&1 | grep -i 'error'"
	echo "Calling command: $COMMAND"
	OUTPUT=$(eval $COMMAND)
	if [ -n "$OUTPUT" ]; then 
		echo "ERROR: $OUTPUT"
		exit 0 
	fi	
}

function assertFileExists {
	if [ ! -f $1]; then
    echo "Error: file not found: $1"
    exit 2
	fi
}

# #######################################################################
# Test for rpm1 
RPMNAME1="toybinprog"
EXECUTABLE1=$RPMNAME1.sh
RPM1=rpms/rpm1/target/debug/source/RPMS/x86_64/toybinprog-1.0-0.x86_64.rpm

assertFileExists $RPM1

callYum remove $RPMNAME1

### create RPM and install
callScons "-c"
callScons rpm1

callYum install $RPM1

echo "Calling installed application: $EXECUTABLE1"
$EXECUTABLE1

callYum remove $RPMNAME1

# #######################################################################
# Test for rpm1
RPMNAME2="app1"
EXECUTABLE2="app1"
RPM2=rpms/rpmapp1/target/debug/source/RPMS/x86_64/app1-0.1-0.x86_64.rpm

assertFileExists $RPM2

callYum remove $RPMNAME2

### create RPM and install
callScons "-c"
callScons rpmapp1

callYum install $RPM2

echo "Calling installed application: $EXECUTABLE2"
$EXECUTABLE2

callYum remove $RPMNAME2

# #######################################################################
# cleanup needed, because all target created as root 
callScons "-c"



