The project explores remote invocations
---------------------------------------
- proxy
- in process by an event queue
- rmi
- .. 

Experiences
-----------
Security kicked in when trying to bind (RMI) a remote object  
  java.security.AccessControlException: access denied ("java.net.SocketPermission" "127.0.0.1:1099" "connect,resolve")
I created a non-secure solution for testing, by "Allowing everything"  
This explains things:  
  http://stackoverflow.com/questions/2427473/java-rmi-accesscontrolexception-access-denied
And here howto solve security with Maven
  http://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html  