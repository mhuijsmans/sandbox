Description project
-------------------
This project explores a possible way to run different type of integration tests.
The approach is the following
- default excludes all tests that existing in this package by excluding 
  all java classes from running against junit (<exclude>**/*.java</exclude>).
- for each type of integration test define a Profile that includes the test to 
  execute. 
  For profile=itest1, <include>**/ittest/App1Test.java</include>
  For profile=itest2, <include>**/ittest/App2Test.java</include>
- Rationale for using profiles is to provide an easier user interface to execute
  a certain type of test, e.g. to execute integrationtest1
  > mvn verify -Pitest2  
  
Test case to execute for this project
-------------------------------------
Open shell an executed following tests:
- > mvn test
  expected output: no test executed
- > mvn install   
  expected output: jar build and installed, no test executed 
- > mvn verify   
  expected output: no test executed
- > mvn verify -Pitest1  
  expected output: App1Test is executed (this is reported in shell)
- > mvn verify -Pitest2  
  expected output: App2Test is executed (this is reported in shell)
- > mvn verify -Ptest2  
  expected output: Nothing is executed, maven gives that profile is not found
      
Adding profiles to integrationtest to make execution easier
         