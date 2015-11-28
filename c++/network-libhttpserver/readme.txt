Introduction
------------
This project explores
https://github.com/etr/libhttpserver
which depends on:
http://www.gnu.org/software/libmicrohttpd/

Installation
============
$ sudo yum install libhttpserver-devel

================================================================================
 Package                   Arch         Version             Repository     Size
================================================================================
Installing:
 libhttpserver-devel       x86_64       0.7.1-2.fc20        updates        24 k
Installing for dependencies:
 libhttpserver             x86_64       0.7.1-2.fc20        updates        79 k
 libmicrohttpd             x86_64       0.9.34-2.fc20       updates        59 k
 libmicrohttpd-devel       x86_64       0.9.34-2.fc20       updates        29 k

Transaction Summary
================================================================================
Install  1 Package (+3 Dependent packages)
  NOTE: 0.7.1 is tag: https://github.com/etr/libhttpserver/tree/v0.7.1

System
------

Experiences
-----------
Use:
- http://127.0.0.1:8080
- http://127.0.0.1:8080/hello
In fact, any URL works.

References
----------
- Instruction on how to create keys for SSL
  and setting up a key store for Java.
  https://github.com/etr/libhttpserver/tree/master/examples
- REST Client (GUI/CLI)
  https://github.com/wiztools/rest-client