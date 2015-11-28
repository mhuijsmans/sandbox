Introduction
------------
Exploring process memory limits/properties for Java.
Exploring process memory limits/properties for JNI.

System
------
any

Experiences
-----------
- Read and next built tests to explore properties and limits,
- A Junit JVM on windows, get 2G of memory. 
  I has no problems increasing JVM size,e g. "-Xms4g","-Xmx4g"  or to 5 GB.
- JNI limit is comtrolled by the amount of virtual memory that a processes can have.
- [martien@localhost bigobjects]$ ulimit -a
  core file size          (blocks, -c) 0
  data seg size           (kbytes, -d) unlimited
  scheduling priority             (-e) 0
  file size               (blocks, -f) unlimited
  pending signals                 (-i) 11833
  max locked memory       (kbytes, -l) 64
  max memory size         (kbytes, -m) unlimited
  open files                      (-n) 1024
  pipe size            (512 bytes, -p) 8
  POSIX message queues     (bytes, -q) 819200
  real-time priority              (-r) 0
  stack size              (kbytes, -s) 8192
  cpu time               (seconds, -t) unlimited
  max user processes              (-u) 1024
  virtual memory          (kbytes, -v) unlimited
  file locks                      (-x) unlimited
ulimit is a bash built in command.  

References
----------
- very interesting article on memory management.
  http://www.ibm.com/developerworks/linux/library/j-nativememory-linux/index.html
  It also states:  
  -- quote --
  The Java heap is likely to be the largest consumer of virtual address space in the process. 
  By reducing the Java heap, you make more space available for other users of native memory.
  -- end quote --
  
- JNI doesn't get memory of Java Heap:
  JNI heap size can not be set.
  http://stackoverflow.com/questions/5986645/java-jni-memory-allocation-partitioning