#!/bin/bash 

# redirection overview / explained:
# http://www.catonmat.net/blog/bash-one-liners-explained-part-three/

# bash argument passing explained
# http://www.cyberciti.biz/faq/unix-linux-bash-function-number-of-arguments-passed/

# GENERAL
# this test suite uses 3 global variables
# > OUTPUT, to hold value of latest scons run
# > LATEST_TIMESTAMP, to hold value of last generated timestamp for code generation
# > TOUCH_TIMESTAMP, to hold value of last generated timestamp for code generation for which "source file" was touched

# DO NOT use spaces when assigning
# so OUTPUT= S...
# doesn't work.
function callSconsCheckError {
	local TIMESTAMP=`date +"%Y-%m-%d %H:%M.%S"`
	echo "[$TIMESTAMP] Calling command: scons $* 2>&1"
	OUTPUT=`scons $* 2>&1`
	
	local OUTPUT_ERROR=`echo $OUTPUT | grep -i 'error'`
	if [ -n "$OUTPUT_ERROR" ]; then 
		echo "ERROR: $OUTPUT_ERROR"
		exit 0 
	fi	
}

function callSconsSimple {
	local TIMESTAMP=`date +"%Y-%m-%d %H:%M.%S"`
	echo "[$TIMESTAMP] Calling command: scons $*"
	scons $*
	local TIMESTAMP=`date +"%Y-%m-%d %H:%M.%S"`	
	echo "[$TIMESTAMP] Command completed"
}

function createNewValue {
	createNewValueNoTouch
	TOUCH_TIMESTAMP=$LATEST_TIMESTAMP
	echo "TOUCH_TIMESTAMP: $TOUCH_TIMESTAMP"
	touch $SRC_FILE1		
}

function createNewValue2 {
	createNewValueNoTouch
	TOUCH_TIMESTAMP=$LATEST_TIMESTAMP
	echo "TOUCH_TIMESTAMP: $TOUCH_TIMESTAMP"
	touch $SRC_FILE2
}

function createNewValueNoTouch {
	# variable inside functions are default global variables
	LATEST_TIMESTAMP=`date +"%T.%N"`
	# use of single qoute prevents substitution. So double quote used.	
	sed "s/VALUE=.*/VALUE=$LATEST_TIMESTAMP/" $SRC_FILE1 > $VALUE_FILE
	echo "LATEST_TIMESTAMP: $LATEST_TIMESTAMP"
}

function checkOutputContainsVALUE {
	local FOUND_VALUE=`echo $OUTPUT | grep "$*"`
	if [ -z "$FOUND_VALUE" ]; then 
		echo "VALUE found: $*"
	else
		echo $OUTPUT
		echo "VALUE not found: $*"
	fi
}

SRC_FILE1=components/comp3/source/dummy_source_file1.my_suffix
SRC_FILE2=components/comp3/source/dummy_source_file2.my_suffix
VALUE_FILE=/tmp/mahu_tmp_file.txt

# ######################################################################

# make sure that the VALUE_FILE exists
cat $SRC_FILE1 > $VALUE_FILE  

# TC: after clean, code shall be generated
callSconsCheckError -c
createNewValueNoTouch
callSconsCheckError comp3_tests verbose=1
checkOutputContainsVALUE "Gencode-$LATEST_TIMESTAMP"

# TC: when source file has changed, code shall be generated. How: comp3_tests
createNewValue
callSconsCheckError comp3_tests verbose=1
checkOutputContainsVALUE "Gencode-$TOUCH_TIMESTAMP"

# TC: source file has not changed, no code shall be generated. How: comp3_tests
# Touch timestamp is used
createNewValueNoTouch
callSconsCheckError comp3_tests verbose=1
checkOutputContainsVALUE "Gencode-$TOUCH_TIMESTAMP"

# TC: when source file has changed, code shall be generated. How: comp5_tests
# comp5 depends on comp3
createNewValue
callSconsCheckError comp5_tests verbose=1
checkOutputContainsVALUE "Gencode-$TOUCH_TIMESTAMP"

# TC: source file has not changed, after clean code shall be generated. How: comp5_tests
# comp5 depends on comp3
callSconsCheckError -c
createNewValueNoTouch
callSconsCheckError comp5_tests verbose=1
checkOutputContainsVALUE "Gencode-$LATEST_TIMESTAMP"

# TC: source file has not changed, after clean code shall be generated. How: app3_tests
# dependency: app3_tests -> comp5 -> comp3
callSconsCheckError -c
createNewValueNoTouch
callSconsCheckError app3_tests verbose=1
checkOutputContainsVALUE "Gencode-$LATEST_TIMESTAMP"

# TC: check for app that generated code is compiled once
callSconsCheckError -c
createNewValue
callSconsCheckError app3 verbose=1
# check by creating a new TOUCH_TIMESTAMP; That should not be used
createNewValueNoTouch
callSconsCheckError app3_tests verbose=1
checkOutputContainsVALUE "Gencode-$TOUCH_TIMESTAMP"

# TC (multiple source files) when 2nd source file changes, code is generated
# extends previous test case 
createNewValue2
callSconsCheckError app3_tests verbose=1
checkOutputContainsVALUE "Gencode-$TOUCH_TIMESTAMP"

