This project explores JNI, using nar-maven-plugin

Lessons learned sofar
---------------------
- testcases that make use of native code shall be implemented as Integration test cases,
  i.e. executed in the maven integration-test phase.
  To make life easy, the pom contains a default build phase.
  <defaultGoal>integration-test</defaultGoal>
  Read: http://mark.donszelmann.org/maven-nar-plugin/nar-integration-test-mojo.html 
- nar generates java.
  In eclipse you need to add the target/nar to your sources directory
  (todo: solve this in maven pom).
- JNI supports: Registering Native Methods On Load
  which makes javah superfluous (I didn't try this yet);
  https://thenewcircle.com/s/post/1292/jni_reference_example 
- calling a method with wrong signature (e.g. (F)V instead of (I)V)
  results in a JVM core dump.
- efficient std::ostringstream std::string and const char * conversions.
  http://stackoverflow.com/questions/1374468/c-stringstream-string-and-char-conversion-confusion  
- JByteArrayPtrIn::JByteArrayPtrIn: value isCopy: true
  I have always (in the few tests that I have run) seen: true. 
  
JNi version
-----------
> #define JNI_VERSION_1_1 0x00010001 
> #define JNI_VERSION_1_2 0x00010002 
> #define JNI_VERSION_1_4 0x00010004 
> #define JNI_VERSION_1_6 0x00010006 
meaning ?
  
Leason nar-maven-plugin
-----------------------  
- to run a single test case
  mvn -Dtest=SpecialTest.java (or set property, see pom file)

JNI essentials
--------------
- Allocation & release
  ref: http://developer.android.com/training/articles/perf-jni.html  
- The offical spec (JNI 6) 
  http://docs.oracle.com/javase/7/docs/technotes/guides/jni/  
  
Throw java-Exception in C++, gives following stack trace
--------------------------------------------------------
java.lang.RuntimeException: c++ error message
	at org.mahu.proto.jnitest.nativewrapper.HelloJNI.testErrorCase1(Native Method)
	at org.mahu.proto.jnitest.JniError1Test.testJniCatchThrowJavaException(JniError1Test.java:113)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    ......
Also the C++ execution continues "after having thrown the Java exception", i.e. it is not
an exception in c++.    

Properties of the JNI interface
-------------------------------
> Data type of methods  
  http://docs.oracle.com/javase/6/docs/technotes/guides/jni/spec/types.html#wp16432
  http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/functions.html
> Reading / write data + checking exception in Java
  https://publib.boulder.ibm.com/iseries/v5r2/ic2924/index.htm?info/rzaha/jniex.htm
> List of topics
  http://www.science.uva.nl/ict/ossdocs/java/tutorial/native1.1/implementing/
> e.g. data type mapping
  ref: http://home.pacifier.com/~mmead/jni/cs510ajp/
> but also reference, c-> java
  ref: http://www.javaworld.com/article/2077513/learn-java/java-tip-17--integrating-java-with-c--.html
> Overview and examples:
  https://thenewcircle.com/s/post/1292/jni_reference_example  
  
Loading dynamic libraries (outside maven)
-----------------------------------------
- loading of dynamic lib's is not standardised (according to ref below; did not investigate (todo)).
  > prior to start JVM or after start of JVM?
  ref: https://groups.google.com/forum/#!msg/maven-nar/1mz9oWj-65U/162A_XI4Ce4J
- One option is the use of java.library.path
  This can be set from commandline
       java -Djava.library.path=<path_to_library(ies)> <main_class>
  or programatically  
       System.setProperty(â€œjava.library.pathâ€�, â€œ/path/to/library(ies)â€�);
- location of libraries (concerned here only with Linux)
  > have an application specific directory, e.g. /opt/app/lib where libraries are stored)
  > store where other dyn. libs are stored. Where ?
  So it would be upto the RPM packaging to take care of this.   

Updating library path (trick)
-----------------------------
http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
Note: it doesn't update the library path uses by the process

Dependant libraries
-------------------
Thread discussed where the jni.dll/so depends on another dll/so.
http://stackoverflow.com/questions/12566732/java-jni-and-dependent-libraries-on-windows  

Maven Standard project structure
--------------------------------
http://mark.donszelmann.org/maven-nar-plugin/philosophy.html 

Example nar pom
---------------
http://trac.imagej.net/browser/launcher/pom.xml?rev=2d521db0afcfca99c906a6c6f0b2ee75a855903f

maven install
-------------
The jar is installed as nar.
[INFO] Installing /home/martien/java/jnitest/target/jnitest-0.1.0-SNAPSHOT.jar 
    to /home/martien/.m2/repository/org/mahu/proto/jnitest/jnitest/0.1.0-SNAPSHOT/jnitest-0.1.0-SNAPSHOT.nar
[INFO] Installing /home/martien/java/jnitest/pom.xml 
    to /home/martien/.m2/repository/org/mahu/proto/jnitest/jnitest/0.1.0-SNAPSHOT/jnitest-0.1.0-SNAPSHOT.pom
[INFO] Installing /home/martien/java/jnitest/target/jnitest-0.1.0-SNAPSHOT-i386-Linux-gpp-jni.nar 
    to /home/martien/.m2/repository/org/mahu/proto/jnitest/jnitest/0.1.0-SNAPSHOT/jnitest-0.1.0-SNAPSHOT-i386-Linux-gpp-jni.nar

properties nar-maven-plugin
---------------------------
- it generates a class NarSystem
- the class narSystem also contains method for running native test cases.
- NarSystem generated in phase: generate-resources.
  Description and rationale at:
  http://mark.donszelmann.org/maven-nar-plugin/nar-system-generate-mojo.html
  
native-maven-plugin: javah & native library
-------------------------------------------
javah produces C header files and C source files from a Java class. 
These files provide the connective glue that allow your Java and C code to interact.
Ref: http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javah.html 
maven tooling:
http://mojo.codehaus.org/maven-native/native-maven-plugin/javah-mojo.html 
http://mojo.codehaus.org/maven-native/native-maven-plugin/examples/share-lib.html 

Used tutorial
-------------
http://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html

References
----------
The Javaâ„¢ Native Interface
Programmerâ€™s Guide and Specification
http://www.soi.city.ac.uk/~kloukin/IN2P3/material/jni.pdf 
Small
http://carfield.com.hk/document/java/tutorial/jni_tutorial.pdf 

Bug in nar document
-------------------
Example http://maven-nar.github.io/usage.html contains error
<packageName>com.mycompany.mypackage</packageName>
->
<narSystemPackage>com.mycompany.mypackage</narSystemPackage> 
Bug fix solved 8 days ago and merged
https://github.com/maven-nar/nar-maven-plugin/issues/105 