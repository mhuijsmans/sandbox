The objective of this project is to prototype the <finaleName> that can be specified in a <build>.
Below is the trace.
From that trace I conclude that
- With the <finalName> one can control the name of the jar in the target directory.
  In fact there is only a single file in the target dir. 
- But the file that is installed in the maven repo conforms is derived from the pom main artifact.  

$ mvn clean install
[INFO] Scanning for projects...
[INFO]
[INFO] Using the builder org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder with a thread
count of 1
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Exploring Maven final name 0.1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ maven-finalname ---
[INFO] Deleting C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\target
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ maven-finalname ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\src\main\r
esources
[INFO]
[INFO] --- maven-compiler-plugin:2.5.1:compile (default-compile) @ maven-finalname ---
[INFO] Compiling 1 source file to C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\target\classes
[INFO]
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ maven-finalname ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\src\test\r
esources
[INFO]
[INFO] --- maven-compiler-plugin:2.5.1:testCompile (default-testCompile) @ maven-finalname ---
[INFO] No sources to compile
[INFO]
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ maven-finalname ---
[INFO] No tests to run.
[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ maven-finalname ---
[INFO] Building jar: C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\target\myFinalName.jar
[INFO]
[INFO] --- maven-install-plugin:2.4:install (default-install) @ maven-finalname ---
[INFO] Installing C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\target\myFinalName.jar to C:\Users\
310160231\.m2\repository\org\mahu\proto\maven\maven-finalname\0.1.0-SNAPSHOT\maven-finalname-0.1.0-SNAPSHOT.jar
[INFO] Installing C:\Users\310160231\Documents\MyMahuGoogleSvn\maven\maven-finalname\pom.xml to C:\Users\310160231\.m2\r
epository\org\mahu\proto\maven\maven-finalname\0.1.0-SNAPSHOT\maven-finalname-0.1.0-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.749 s
[INFO] Finished at: 2014-05-30T13:43:32+01:00
[INFO] Final Memory: 13M/304M
[INFO] ------------------------------------------------------------------------ 