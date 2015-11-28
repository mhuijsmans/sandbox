The objective of the script is to create a subset of boost 
with only files required by the application.
It does this by parsing the compile error and copying the missing file
to a directory that is used in the build.
