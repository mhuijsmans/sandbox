This project explores building jni library with scons.

System
------
fedora-20

Preperation
-----------
- install scons is if needed.
  $ sudo yum install scons
- set JAVA_HOME (see ref)
  I needed following command to get JAVA_HOME set after starting a new terminal.
- Updatwed  /home/username/.bashrc
  Added following 2 lines at end:
  # set JAVA_HOME
  source /etc/environment

Hello-world
-----------
- Followed the example, which after
  $ scons
  has generated generated the header file from java, compiled and build the .so.
  Quick fast and easy.
- Scons has a complaint that the used BuildDir() is obsolete. I didn't solve this.
- Finally the test as the howto suggested. everything worked like a charm

I stopped after the above steps, because I wanted to have a JUnit test suite to test. For me that was maven territory. I didn't invest in scons here.

References
----------
Scons jni howto, including trying.
http://www.scons.org/wiki/JavaNativeInterface

Set Java-home
http://askubuntu.com/questions/175514/how-to-set-java-home-for-openjdk
