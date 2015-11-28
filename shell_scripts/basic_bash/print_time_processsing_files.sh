#!/bin/bash 

# print date
# http://www.cyberciti.biz/faq/linux-unix-formatting-dates-for-display/         

# zip/unzip
# http://www.cyberciti.biz/tips/how-can-i-zipping-and-unzipping-files-under-linux.html

# tar.gz
# http://www.tecmint.com/18-tar-command-examples-in-linux/

echo $(date +"%m-%d-%y:%H:%M:%S.%N")
echo "Cat&grep of log2.xml, number of matches"
cat log2.xml | grep aapfamily |  wc -l

echo $(date +"%m-%d-%y:%H:%M:%S.%N")
echo "Cat&grep of log2.zip, number of matches"
unzip -c log2.zip log2.xml | grep aapfamily |  wc -l

echo $(date +"%m-%d-%y:%H:%M:%S.%N")
echo "Cat&grep of log2.tar.gz, number of matches"
tar -xOvf log2.tar.gz log2.xml | grep aapfamily |  wc -l

echo $(date +"%m-%d-%y:%H:%M:%S.%N")
echo "Cat&grep of XFSlog_20130724.xml, number of matches"
cat XFSlog_20130724.xml | grep aapfamily |  wc -l

echo $(date +"%m-%d-%y:%H:%M:%S.%N")