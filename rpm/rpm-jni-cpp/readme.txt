Goal
----
This project creates an RPM for an application that uses a native library.
It includes a project to create a stub lib and normal lib of an C++ API.
Tests are executed against the stub and the normal lib.
Finally an RPM is created which also include the normal lib.

This project is a extended version of the rpm-jni project, upon which this was based.

System
------
Target os: linux

CPP project
-----------
Own folder: cpp
output stored in
/repo_cpp/inc
/repo_cpp/lib
/repo_cpp/stublib

Experiences
------------
- When importing projects into Eclipse, you need to add the library project
  to the build-path of the app.
  Maven itself is fine with the nar. Appearanlt     

References
----------


