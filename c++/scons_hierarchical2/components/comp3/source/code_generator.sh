#!/bin/bash 

echo "### code_generator.sh, targetdir=$1, codeGenSourceDoc=$2"
# $2 is not used

if [[ $2 == *"dummy_source_file1.my_suffix"* ]]
then
	CPP_CLS="GenCode"
else
	CPP_CLS="GenCode2"
fi

H_FILE="$1/$CPP_CLS.h"
CPP_FILE="$1/$CPP_CLS.cpp"

VALUE_FILE=/tmp/mahu_tmp_file.txt
VALUE=`cat $VALUE_FILE | grep 'VALUE=' | sed 's/VALUE=//'`
if [ -z "$VALUE" ]; then
	VALUE="????????????"
fi	

echo "### code_generator.sh, CPP_CLS=$CPP_CLS, VALUE=$VALUE"

echo ". generating file $H_FILE"
echo "// This is generated code"  > $H_FILE
echo "class $CPP_CLS { public: $CPP_CLS(); };" >> $H_FILE

echo ". generating file $CPP_FILE'"
echo '// This is generated code' > $CPP_FILE
echo '#include <iostream>' >> $CPP_FILE
echo "#include \"$CPP_CLS.h\"" >> $CPP_FILE
echo "$CPP_CLS::$CPP_CLS() { std::cout << \"$CPP_CLS-$VALUE\"<< std::endl; }" >> $CPP_FILE

echo "### code_generator.sh, listing the generated files"
ls -al $1

echo ". generating completed"