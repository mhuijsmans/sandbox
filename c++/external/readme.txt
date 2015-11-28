This files describes the content of this directory

gtest
- download version 1.7.0: https://code.google.com/p/googletest/
- open shell, goto gtest-1.7.0/make
- $ make
  this will generate the libraries. gtest is now ready for use.
  gtest-1.7.0/README provides more details on make command.
  
gmock  
- download version 1.7.0: https://code.google.com/p/googlemock/
- open shell, goto gtest-1.7.0/make
- $ make

boost_1_56_0
- getting started page: http://www.boost.org/doc/libs/1_56_0/more/getting_started/unix-variants.html
- download page: http://www.boost.org/users/history/version_1_56_0.html
  to build on linux, select the boost_1_56_0.tar.gz.
  That also contains the linux build tools.
  The .zip does not contains linux build tools.
- open shell and goto target dir, here external.
  Extract to current directory.
  $ tar -zxvf <path_to_tar_gz>/boost_1_56_0.tar.tar.gz
- to build, I entered
  $ ./bootstrap.sh
  $ ./b2

rapidxml-1.13
- only unzip needed

cpp-netlib
- http://cpp-netlib.org/
- downloaded 0.11.0
- unzipped.
- To build: use readme.srt in zip.
     or: http://cpp-netlib.org/0.11.0/getting_started.html#getting-started
	 (getting started is best source !).
  $ mkdir cpp-netlib-build
  $ cd cpp-netlib-build
  $ cmake -DCMAKE_BUILD_TYPE=Debug -DCMAKE_C_COMPILER=gcc -DCMAKE_CXX_COMPILER=g++ -DBOOST_LIBRARYDIR=../boost_1_56_0/lib/linux ../cpp-netlib-0.11.0-final/
    will detect OpenSSL and next include that 
  $ make
  $ make test   (to run tests)
  $ (skipped) sudo make install

libcurl-devel-7.38.0
- note: do this under linux on a linux file system (not shared folder), 
  because the RPM contains a dynamic link, which doesn't unzip correctly.
- "unzipped" version of libcurl-devel-7.38.0-2.fc22.x86_64.rpm
  downloaded from: http://mirror.vutbr.cz/fedora/development/rawhide/x86_64/os/Packages/l/
- unzip with 7zip  

libhttpserver
- download zip from:
  https://github.com/etr/libhttpserver
- Note: do next steps on Linux with Linux filesystem
- unzipped
  Followed instruction on above link.
  ./bootstrap reports about missing commands.
  $ sudo yum install automake autoconf libtool

libmicrohttpd
http://www.gnu.org/software/libmicrohttpd/
- for SSL/TLS support requires libgcrypt and libgnutls.
- To build:
  http://www.gnu.org/software/libmicrohttpd/manual/libmicrohttpd.html#microhttpd_002dintro
  standards procedre
  $ ./configure
  $ make
  $ sudo make install
    Installs files on file system

Note:building libhttpserver from source failed, because configure could not find the installed libmictohttp
  
============== CPP Netlib Build log ===
IBRARYDIR=../boost_1_56_0/lib/linux ../cpp-netlib-0.11.0-final/
-- The C compiler identification is GNU 4.8.3
-- The CXX compiler identification is GNU 4.8.3
-- Check for working C compiler: /usr/bin/gcc
-- Check for working C compiler: /usr/bin/gcc -- works
-- Detecting C compiler ABI info
-- Detecting C compiler ABI info - done
-- Check for working CXX compiler: /usr/bin/g++
-- Check for working CXX compiler: /usr/bin/g++ -- works
-- Detecting CXX compiler ABI info
-- Detecting CXX compiler ABI info - done
-- Boost version: 1.54.0
-- Found the following Boost libraries:
--   unit_test_framework
--   system
--   regex
--   date_time
--   thread
--   filesystem
--   program_options
--   chrono
--   atomic
-- Found OpenSSL: /usr/lib64/libssl.so;/usr/lib64/libcrypto.so (found version "1.0.1e") 
-- Looking for include file pthread.h
-- Looking for include file pthread.h - found
-- Looking for pthread_create
-- Looking for pthread_create - not found
-- Looking for pthread_create in pthreads
-- Looking for pthread_create in pthreads - not found
-- Looking for pthread_create in pthread
-- Looking for pthread_create in pthread - found
-- Found Threads: TRUE  
-- Configuring done
-- Generating done
-- Build files have been written to: /home/martien/mahu_googlecode/c++/external/cpp-netlib-build
====================  
== cpp-netlib (part of make output; I included line breaks for clarity) ===
cd /home/martien/mahu_googlecode/c++/external/cpp-netlib-build/libs/network/src && 
	/usr/bin/g++  -DBOOST_NETWORK_DEBUG -DBOOST_NETWORK_ENABLE_HTTPS -Wall -g 
	-I/home/martien/mahu_googlecode/c++/external/cpp-netlib-0.11.0-final    
	-o CMakeFiles/cppnetlib-client-connections.dir/client.cpp.o 
	-c /home/martien/mahu_googlecode/c++/external/cpp-netlib-0.11.0-final/libs/network/src/client.cpp

