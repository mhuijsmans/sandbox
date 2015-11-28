
Introduction
------------
The project explores hierarchical builds with scons

Open Issues
-----------
> none

Supported
---------
- different type of builds: release, debug, profile
- support
  > (test-)components
  > application
- easy configuration / declaritive style 
	> dependsOn(..)
  > dependsOnInclude(..)
	> dependsOnGTest()
  > addSconsTargetToRunTests(..)
  > visitBuildDirectories(..)
  > visitSubDirectories(..)
- static / dynamic library	
  > create (scons)
  > test cases that uses static libraries
- maintainability / trouble shoot
  > verbose options, to show configuration
- modularity: use of configuration files for 
  > functions
  > components
- builds targets
  > executables + libraries: scons (no arguments)
  > single application: scons appname
  > single library: scons libname
  > run single test suite: scons comp1_tests  
  > run all 
    > tests: scons all_tests
    > libs:  scons all_libs
    > apps:  scons all_apps
    > all:   scons all
- testing
  - target for a single components / application test suite.
    gest can be used to select individual test cases.
  > run all test cases with a single command: scons all_tests\
- invocation tooling for code generation
  code generated in target/generated_source
- clean all target folders (debug / profile / release)  
- Rpm's
  > for different type of builds
  > svn version number
- transitieve dependencies
- removed need for Glue Sconscripts. Use is still supported
- DependsOnExternal

Todo
----
- static / dynamic libraries
  >- test case that uses dynamic linking
     = requires rpath or alternative
       use single location for dynamic libraries
  - forcing static static linking of library(ies) using compile / link flags
- Integration test

Scons info
----------
 - '#' character in SCons means relative to the project root directory (which is where the SConstruct build script is located)

Scons examples/info
-------------------
- http://www.scons.org/doc/0.98.5/HTML/scons-user/x3520.html
  scons -n -tree=[all | status]
- Depends(what, dependsOn): http://stackoverflow.com/questions/8766450/scons-run-target
- main = opt.Program('main.cpp')
  env.Alias(targetName, main, main[0].abspath)
  defines an alias "targetName" that depends on main.
  $ scons targetName 
  will build main and next execute it
- recipies: http://www.scons.org/wiki/SconsRecipes  
- fastbutton: http://www.scons.org/wiki/GoFastButton
- scons custom configuration
  http://www.scons.org/doc/1.0.1/HTML/scons-user/x3627.html
- customer clean
  http://scons.org/doc/2.1.0/HTML/scons-user/x3159.html  
  http://stackoverflow.com/questions/2473808/scons-does-not-clean-all-files?rq=1
- difference between: emitter, a source_scanner and a target_scanner?  
  http://scons.1086193.n5.nabble.com/Difference-emitter-scanner-td29868.html
- Nice scons / python example that uses a class
  http://sourceforge.net/p/libpninx/code/ci/master/tree/SConstruct
- SourceRootDirectory + '/external/lib/%s_%s/libEGL' % (Platform, Configuration),
- scons create zip
  http://stackoverflow.com/questions/8951719/scons-how-to-add-a-target-for-creating-a-zip-file-that-contains-source-files?rq=1

Scons quirks
------------
- scons copy doesn't copy files outside own directory
  scons command are targetted towards targets
  ref: http://stackoverflow.com/questions/16687684/how-do-i-copy-files-from-project-directory-to-outside-project-directory

RPM
---
- I see scons and RPM here.
  https://chromium.googlesource.com/native_client/src/third_party/scons-2.0.1/+/0c7344ddfa5f43bd4bdfbe30cf60df9eef1598b7/engine/SCons/Tool/packaging/rpm.py  
- a exampel:
  http://stackoverflow.com/questions/13040087/what-is-a-good-rpm-building-tool   

sed
---
used ref: http://www.grymoire.com/Unix/Sed.html
To test replacing the value after =
$ echo "VALUE=10" | sed 's/VALUE=.*/VALUE=5/'
$ value=5
To test hetting value after =
$ echo "VALUE=10" | grep 'VALUE=' | sed 's/VALUE=//'
$ 10

Linker
------
> The order in which information is provided to the linker matters. It works from left to right
  I have expereienced that ".. lcomp3 -lcomp3" results in "undefined reference to .." whereas 
  ".. lcomp5 -lcomp3" resulted in no errors. 
  ref: http://stackoverflow.com/questions/12272864/linker-error-on-linux-undefined-reference-to 

references
----------
- IBM developer works articel on rpm, including triggers
  http://www.ibm.com/developerworks/linux/library/l-rpm2/index.html