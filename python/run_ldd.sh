#!/usr/bin/python

class LddDetectedDynamicLibrary:

    def __init__(self, library, pathLibrary):
        self.Debug('LddDetectedDynamicLibrary.ctor()')
        self.library = library.strip()
        self.pathLibrary = pathLibrary.strip()

    def GetLibrary(self):
        return self.library

    def GetLibraryPath(self):
        return self.pathLibrary

    def Debug(self, msg):
        pass
        # print(msg)

    # __str__(self) for readable representation
    def __str__(self):
        return 'DetectedLibrary[library=%s libraryPath=%s' % (self.library, self.pathLibrary)

    # __repr__() for unambiguous representation.

class Ldd:

    def __init__(self):
        self.Debug('Ldd.ctor()')
        self.detectedLibrary = []

    def GetDynamicLibraries(self, pathExecutable):
        self.Debug('GetDynamicLibraries pathExecutable=%s' % pathExecutable)
        import subprocess
        p = subprocess.Popen(['ldd', pathExecutable], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        # out is a character string, convert to lines
        outAsLines = out.split('\n')
        self.Debug ('StdOut: %s' % out)
        self.Debug ('StdErr: %s' % err)
        self.Debug ('Return code: %s' % p.returncode)
        assert p.returncode==0, "Failed to execute ldd executable=%s; returncode=%s" % (pathExecutable, p.returncode)
        for line in outAsLines:
            # there are lines without =>
            # Example: /lib64/ld-linux-x86-64.so.2 (0x0000003b97200000)
            self.Debug('Line: %s' % line)
            if ' => ' in line:
                parts = line.split(' ')
                self.Debug ('Library: %s path: %s' % (parts[0], parts[2]))
                self.detectedLibrary.append(LddDetectedDynamicLibrary(parts[0], parts[2]))

    def Debug(self, msg):
        pass
        # print(msg)

    def GetDetectedLibrary(self, libraryName):
        for dl in self.detectedLibrary:
            if dl.GetLibrary().startswith(libraryName):
                self.Debug ('Match: %s against: %s' % (libraryName, dl))
                return dl
        self.Debug ('No match for: %s' % libraryName)
        return None

# ##################################################################
# CommandLine

class CommandLine:

    def __init__(self):
        print('CommandLine.ctor()')
        self.executable = None

    def ProcessCommandLineSettings(self):
        import getopt, sys, os
        msg = 'Usage: run_ldd.py -e/--executable path_to_executable'
        try:
            opts, args = getopt.getopt(sys.argv[1:],"he:",["executable="])
        except getopt.GetoptError:
            print msg
            sys.exit(2)
        for opt, arg in opts:
            if opt == '-h':
                print msg
            elif opt in ("-e", "--executable"):
                self.executable = arg
        if self.executable == None:
            print 'Error: invalid command'
            print msg
            sys.exit(2)
        print 'Using: executable: "', self.executable

    def PathExecutable(self):
        return self.executable

# ######################################################################################
# main

try:
    print('Main: ENTER')

    commandline = CommandLine()
    commandline.ProcessCommandLineSettings()

    ldd = Ldd()
    ldd.GetDynamicLibraries(commandline.PathExecutable())
    print ('Detected library: %s' % ldd.GetDetectedLibrary('libboost_chrono.so'))

except Exception as e:
    import traceback
    print('ERROR: caught exception in main loop: %s' % e)
    traceback.print_exc()

finally:
    print('Main: finally')
    print('Main: LEAVE')