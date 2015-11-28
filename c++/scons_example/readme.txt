The project explores the build tools scons

Experience
----------
- My helloworld failed with a Error 127.
  Which means g++ is not installed.
  $ sudo yum install gcc-c++
  solved this.
- scons feels good

Reference
---------
http://www.scons.org/doc/production/HTML/scons-user.html#idp87584

Code snipets
ref: http://comments.gmane.org/gmane.comp.programming.tools.scons.user/265 
src = [ ... many .cpp files ]
objs = [env.StaticObject(source=s) for s in src]
shobjs = [env.SharedObject(source=s) for s in src]
Return("objs shobjs")

Dynamically build dependency model between .c ad .h files.
http://fossies.org/linux/misc/madagascar-1.6.4.tar.gz/madagascar-1.6.4/pens/main/SConstruct 
