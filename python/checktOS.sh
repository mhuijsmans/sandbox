#!/usr/bin/python

# return if OperatingSystem is windows
def isOSWindows(env):
	import sys
	return sys.platform == 'win32'
env.AddMethod(isOSWindows, "IsOSWindows");	

# return if OperatingSystem is linux
def isOSLinux(env):
	import sys
	return sys.platform == 'linux'
env.AddMethod(isOSLinux, "IsOSLinux");	

# return if OperatingSystem is linux with a windows mapped file system
# Than certain linux features, e.g. dynamic link, stat do not work.
# DO NOT USE. Not tested
def isOSLinuxWithMappedFileSystem(env):
	if env.IsOSLinux():
		import os
		testFile = os.path.join(env.TargetDir(),'isOSLinuxWithMappedFileSystem.testfile' )
		open(testFile, 'a').close()
		mode = os.stat(testFile).st_mode
		return True
	else:
		return False
env.AddMethod(isOSLinuxWithMappedFileSystem, "IsOSLinuxWithMappedFileSystem");