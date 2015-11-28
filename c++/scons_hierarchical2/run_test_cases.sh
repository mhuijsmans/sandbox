#!/bin/bash

# redirection overview / explained:
# http://www.catonmat.net/blog/bash-one-liners-explained-part-three/

# bash argument passing explained
# http://www.cyberciti.biz/faq/unix-linux-bash-function-number-of-arguments-passed/

# important: DO NOT use spaces when assigning
# so OUTPUT= S...
# doesn't work.
function callScons {
	COMMAND="scons -j 8 $* 2>&1 | grep -v ' -Werror ' | grep -i 'error'"
	TIMESTAMP=`date +"%Y-%m-%d %H:%M.%S"`
	echo "[$TIMESTAMP] Calling command: $COMMAND"
	OUTPUT=$(eval $COMMAND)
	if [ -n "$OUTPUT" ]; then
		echo "ERROR: $OUTPUT"
		exit 0
	fi
}

### basic tests
callScons "-c"
callScons "-c" verbose=1
callScons
callScons build=debug
callScons build=profile
callScons build=debug gcov=1

### build all components
callScons "-c"
callScons comp1 comp2 comp3 comp4 comp5 comp6 app1 app2 app3

### Individual components
callScons "-c"
callScons comp1

callScons "-c"
callScons comp2

# comp3 is used for code generation
callScons "-c"
callScons comp3

callScons "-c"
callScons comp4

callScons "-c"
callScons comp5

callScons "-c"
callScons comp6

callScons "-c"
callScons app1

callScons "-c"
callScons app2

callScons "-c"
callScons app3

### tests
callScons "-c"
callScons comp1_tests

callScons "-c"
callScons comp2_tests

callScons "-c"
callScons comp3_tests

callScons "-c"
callScons comp4_tests

callScons "-c"
callScons comp5_tests

callScons "-c"
callScons comp6_tests

### ALL
callScons "-c"
callScons all_tests

callScons "-c"
callScons all_components

callScons "-c"
callScons all_apps

callScons "-c"
callScons all_rpms

callScons "-c"
callScons all

callScons "-c"
callScons all install

callScons "-c"
callScons all gcov=1


