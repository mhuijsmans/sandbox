# General Utilities

# #####################################
# Test

def TestAssert(flag, msg):
    if not flag:
        Log(msg)
        raise Exception(msg)

# #####################################
# Time functions

def Now():
    from datetime import datetime
    return str(datetime.now())

# Create timestamp of format: 20141124-15:30:09
# This is a singleton, i.e. timestamp is determined once
def FileTimeStamp():
    global varFileTimeStamp
    try:
        return varFileTimeStamp
    except NameError:
        varFileTimeStamp = Now().replace('-','',2).split('.')[0].replace(' ','-').replace(':','')
        return varFileTimeStamp

# #####################################
# Log functions

def CreateLogFilePrivate(name):
    name = CreateLogFileNamePrivate(name)
    Log('CreateLogFile opening logfile: %s' % name)
    logfile = file(name,'w')
    return logfile

def CreateLogFileNamePrivate(name):
    import posixpath
    from os.path import expanduser
    dir = './log'
    if not os.path.exists(dir):
        Log('CreateLogFile creating directory: %s' % dir)
        os.makedirs(dir)
    name = posixpath.normpath( dir+'/%s_logfile_%s.txt' % (name, FileTimeStamp()) )
    Log('Created name for a logFile: %s' % name)
    return name

logtofile = False
def OpenLogFile(fileName):
    global logfile, logtofile
    logfile = CreateLogFilePrivate(fileName)
    logtofile = True

def CloseLogFile():
    global logfile, logtofile
    if logtofile:
        logfile.close()
        logtofile = False

def LogAndPrint(msg):
    global logtofile
    Log(msg)
    if logtofile:
        print '%s %s' %(Now(), msg)

def Log(msg):
    global logfile, logtofile
    if logtofile:
        logfile.write('[%s] [] [trace] %s\n' %(Now(), msg))
    else:
        print '%s %s' %(Now(), msg)
