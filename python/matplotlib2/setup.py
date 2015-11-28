#!/usr/bin/python

# ##################################################################
# CommandLine

class CommandLine:

    def __init__(self):
        print 'CommandLine.ctor()'
        self.build = None
        self.svnbasedir = None

    def ProcessCommandLineSettings(self):
        import getopt, sys
        msg = 'Usage: setup.py -b/--build <debug|release> -d/--svnbasedir <svn dir with cpp/external directory>'
        try:
            opts, args = getopt.getopt(sys.argv[1:],"hb:d:",["build=","svnbasedir="])
        except getopt.GetoptError:
            print msg
            sys.exit(2)
        for opt, arg in opts:
            if opt == '-h':
                print msg
                sys.exit()
            elif opt in ("-b", "--build"):
                self.build = arg
            elif opt in ("-d", "--svnbasedir"):
                self.svnbasedir = arg
        if self.build == None or self.svnbasedir == None:
            print 'Error: invalid command'
            print msg
            sys.exit(2)
        print 'Using, build: "', self.build
        print 'Using: svnbasedir: "', self.svnbasedir

    def BuildType(self):
        return self.build

    def SvnBaseDir(self):
        return self.svnbasedir

# ##################################################################
# MAIN

commandline = CommandLine()
commandline.ProcessCommandLineSettings()

print 'Setup environment completed, Enjoy'