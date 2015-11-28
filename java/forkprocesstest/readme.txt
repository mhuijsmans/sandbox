Introduction
------------
This project explores
- forking a process
- forking a process that is a copy of own process 

System
------

Experiences
-----------
- test that forks a java process 
  with correct mainclass takes about 200 ms
  with wrong mainclass  takes about 2 sec
- exitcode (reported by java process.exitValue())
  0: normal termination
  1: returned when java call destoy() on process
  So I can use 0 but not 1.   

References
----------
This discusses forking experiences and strategies. It discusses sharing memory between processes. 
Cool.
http://bryanmarty.com/2012/01/14/forking-jvm/ 

Forking in any language: http://rosettacode.org/wiki/Fork#Java

Forking on Linux (really interesting, but non-trvial)
http://pubs.opengroup.org/onlinepubs/000095399/functions/fork.html