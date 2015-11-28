import os
Import('env')

# ############################################################
# small support functions

# return if verbose has been set on command line with value != 0
def debug(env,msg):
  if env.IsDebug():
    print msg
env.AddMethod(debug, "Debug")

def isDebug(env):
  return env['VERBOSE'] !='0'
env.AddMethod(isDebug, "IsDebug")

def isTrace(env):
  return env['VERBOSE'] =='2'
env.AddMethod(isTrace, "IsTrace")

def printStackTrace(env):
    import traceback
    traceback.print_exc(file=sys.stdout)
env.AddMethod(printStackTrace, "PrintStackTrace")

def fatalError(env, msg):
    import traceback, sys
    print '****************************************\n FatalError: %s ' % msg
    # traceback didn't print useful info; investigate using 'unrescolved dependency'
    #traceback.print_exc(file=sys.stdout)
    exit(1)
env.AddMethod(fatalError, "FatalError")

# ############################################################
# Platform

# return if OperatingSystem is windows
def IsOSWindows(env):
    import sys
    return sys.platform == 'win32'
env.AddMethod(IsOSWindows, "IsOSWindows")

# return if OperatingSystem is linux
def IsOSLinux(env):
    import sys
    return sys.platform == 'linux' or sys.platform == 'linux2'
env.AddMethod(IsOSLinux, "IsOSLinux")

# ############################################################
# SystemCall

def systemCall(env, command):
    import os
    env.Debug('Command: %s' % command)
    result = os.system(command)
    env.Debug('Command result (0=ok): %s' % result)
    return result
env.AddMethod(systemCall, "SystemCall")

def systemCallWithResultCheck(env, command, msg):
    if env.SystemCall(command) != 0:
        env.FatalError(msg)
env.AddMethod(systemCallWithResultCheck, "SystemCallWithResultCheck")

# ############################################################
# Testing datatype of a variable

def isOfTypeString(env, var):
    return isinstance(var, basestring)
env.AddMethod(isOfTypeString, "IsOfTypeString")

def isOfTypeList(env, var):
    return type(var) is list
env.AddMethod(isOfTypeList, "IsOfTypeList")

# ############################################################
# disk IO: different operations

# create empty file (Linux style touch) and directories (if not existing)
def touch(env, file):
    import os
    env.MkdirRecursive(os.path.dirname(file))
    open(file, 'a').close()
    # if file exists, open(..) doesn't change modified time.
    # Next line updates last modified to current time
    os.utime(file,None)
    lastModifiedTime = os.path.getmtime(file)
    env.Debug('Touch: %s, lastModifiedTime: %s' % (file,lastModifiedTime))
env.AddMethod(touch, "Touch")

def mkdirRecursive(env, path):
    assert path is not None, 'Invalid path, value is None'
    assert isinstance(path, basestring), 'Invalid path, must be of type string'
    assert path != '', 'Invalid path, is empty string'
    env.Debug('MkdirRecursive: %s' % path)
    import os
    dirname = os.path.dirname(path)
    if not os.path.exists(dirname):
        env.MkdirRecursive(dirname)
    if not os.path.exists(path):
        os.mkdir(path)
env.AddMethod(mkdirRecursive, "MkdirRecursive")

# delete a directory named : <something>/target
def deleteTargetDirectory(env, directoryToClean):
    env.Debug('DeleteTargetDirectory, dir: %s' % directoryToClean)
    import shutil, os.path
    if os.path.exists(directoryToClean) and os.path.isdir(directoryToClean):
        # basic sanity check
        if os.path.basename(directoryToClean) != 'target':
            print '. FATAL, invalid clean directory: ',directoryToClean
            Exit(2)
        print '. cleaning target directory: ',directoryToClean
        shutil.rmtree(directoryToClean, ignore_errors=True)
env.AddMethod(deleteTargetDirectory, "DeleteTargetDirectory")

# create a symbolic link (if not existing yet)
def createSymLink(env, source, symLinkName):
    import os
    env.Debug('CreateSymLink, source: %s' % source)
    env.Debug('CreateSymLink, symLinkName: %s' % symLinkName)
    if os.path.lexists(symLinkName) == False:
        symLinkDir = os.path.dirname(symLinkName)
        # after a clean, the build dir doesn't exist. Create it for symlink.
        env.MkdirRecursive(symLinkDir)
        os.symlink(source, symLinkName)
env.AddMethod(createSymLink, "CreateSymLink")

# ############################################################
# disk IO: Reading and writing text data to file

def readStringFromFile(env, filename):
    filedata = None
    with open(filename, 'r') as file:
        filedata = file.read()
    return filedata
env.AddMethod(readStringFromFile, "ReadStringFromFile")

def writeStringToFile(env, filename, filedata):
    # Write
    with open(filename, 'w') as file:
        file.write(filedata)
env.AddMethod(writeStringToFile, "WriteStringToFile")

# ############################################################
# Disk IO: compare date file1(must exist) and file2 (may exists)
# return true when
# > file 2 doesn't exist
# > date file 1 < date file 2
def isDateFile1MoreRecentThanDateFile2(env, file1, file2):
    import os
    env.Debug('IsDateFile1MoreRecentThanDateFile2:\n. file1: %s\n. file2: %s' % (file1,file2))
    if os.path.exists(file1):
        if os.path.exists(file2):
            if os.path.isfile(file1) and os.path.isfile(file1):
                lastModifiedTimeFile1 = os.path.getmtime(file1)
                lastModifiedTimeFile2 = os.path.getmtime(file2)
                env.Debug('IsDateFile1MoreRecentThanDateFile2:\n. lastModifiedTimeFile1: %s\n. lastModifiedTimeFile2: %s' % (lastModifiedTimeFile1,lastModifiedTimeFile2))
                return lastModifiedTimeFile1 > lastModifiedTimeFile2
            else:
                env.FatalError('file1 and/or file2 is not a file\n. file1: %s\n. file2: %s' % (file1,file2))
        else:
            return True
    else:
        env.FatalError('file does not exists: %s' %file1)
env.AddMethod(isDateFile1MoreRecentThanDateFile2, "IsDateFile1MoreRecentThanDateFile2")

# ############################################################
# copied from: http://stackoverflow.com/questions/12518715/how-do-i-filter-an-scons-glob-result
def filtered_glob(env, pattern, omit=[],
  ondisk=True, source=False, strings=False):
    return filter(
      lambda f: os.path.basename(f.path) not in omit, env.Glob(pattern))
env.AddMethod(filtered_glob, "FilteredGlob");

# this method enable to see when Glob(..) is invoked and what result is.
# en thereby better understand scons implementation.
def globWithDebug(env, name):
    env.Debug('GlobWithDebug: %s' % name)
    globResult = env.Glob(name)
    env.Debug('GlobWithDebug, result: %s' % (", ".join(str(x) for x in globResult)))
    return globResult
env.AddMethod(globWithDebug, "GlobWithDebug")

# ############################################################
# customer cleaning

# recursiveGlob: http://stackoverflow.com/questions/2186525/use-a-glob-to-find-files-recursively-in-python
def recursiveGlob(rootdir='.', pattern='*'):
    import fnmatch
    import os
    return [os.path.join(looproot, filename)
            for looproot, _, filenames in os.walk(rootdir)
            for filename in filenames
            if fnmatch.fnmatch(filename, pattern)]

if env.GetOption('clean'):
    sconsRootDir = Dir('#').abspath
    env.Clean('deepclean', recursiveGlob(sconsRootDir, '*.gcda'))
    env.Clean('deepclean', recursiveGlob(sconsRootDir, '*.gcno'))
    env.Clean('deepclean', recursiveGlob(sconsRootDir, '*.os'))

# ############################################################

if env.IsTrace():
    print env.Dump()  # dump whole env