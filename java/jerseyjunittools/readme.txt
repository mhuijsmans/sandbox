Introduction
------------
This project contains tools to make REST Testing with Jersey easier.
The tools include
- RestServiceRule implements TestRule
  which makes starting / stopping webserver easier
- 2 annotations
  > RestResourceInTest 
  > RestProviderInTest
  
Support for Spring
------------------
- Provider is created automatically

Experience
----------
- For spring I created instance of same class SpringResourceProvider that generates different
  instances. But Jersey sees this as the same component. 
- Jul 20, 2014 10:05:54 PM org.glassfish.jersey.internal.Errors logErrors
  WARNING: The following warnings have been detected: WARNING: Cannot new create registration for component type class org.mahu.proto.jerseyjunittools.SpringResourceProvider: Existing previous registration found for the type.
- Delay measured by: HelloWorldTest.testDelayMethodGet()
  max:   1, elapsedTime:  221, avg (ms): 221
  max:  10, elapsedTime:  488, avg (ms):  48
  max: 100, elapsedTime: 2476, avg (ms):  24
  max: 500, elapsedTime: 6015, avg (ms):  12  
 