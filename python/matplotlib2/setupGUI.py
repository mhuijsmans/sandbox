#!/usr/bin/python

import Tkinter, Tkconstants, tkFileDialog
from  Tkinter import Label, Button, Radiobutton, W, IntVar
import tkMessageBox

# ##########################################################
# Generic functions
def HomeDir():
    from os.path import expanduser
    return expanduser("~")

def AssertLinuxPlatform():
    from sys import platform as _platform
    assert _platform == "linux" or  _platform == "linux2", "This application is developed for Linux, platform= %s" % _platform

# ##########################################################
# Constructing the GUI
class Configuration:

    def __init__(self):
        self.cppExternalDir = HomeDir()
        self.buildType = 'debug'

    def SetBuildTypeDebug(self):
        self.SetBuildType('debug')

    def SetBuildTypeRelease(self):
        self.SetBuildType('release')

    def SetBuildType(self, type):
        print 'BuildType: %s' % type
        assert type=='debug' or type=='release', 'Invalid type: %s' % type
        self.buildType = type
        self.SaveConfiguration()

    def SetCppExternalDir(self, dir):
        print 'CppExternalDir: %s' % dir
        import os.path
        assert os.path.exists(dir) and os.path.isdir(dir), 'Not a directory: %s' % dir
        self.cppExternalDir = dir
        self.SaveConfiguration()

    def IsBuildTypeDebug(self):
        return self.buildType == 'debug'

    def GetBuildType(self):
        return self.buildType

    def GetCppExternalDir(self):
        return self.cppExternalDir

    def SaveConfiguration(self):
        text_file = open(self.FileName(), "w")
        text_file.write("buildtype=%s\n" % self.GetBuildType())
        text_file.write("cppexternaldir=%s\n" % self.GetCppExternalDir())
        text_file.close()

    def ReadConfiguration(self):
        filename = self.FileName()
        import os.path
        if os.path.exists(filename) and os.path.isfile(filename):
            text_file = open(self.FileName(), "r")
            self.SetBuildType( text_file.readline().strip().split('=')[1])
            self.SetCppExternalDir( text_file.readline().strip().split('=')[1])
            text_file.close()

    def FileName(self):
        return HomeDir() + '/.sgs_pt_gui'

# ##########################################################
# Constructing the GUI

class Gui:

    top = None
    var = None
    dirLabel = None
    configuration = Configuration()

    def __init__(self):
        pass

    @staticmethod
    def BuildType():
        if Gui.var.get() == 1:
            Gui.configuration.SetBuildTypeDebug()
        if Gui.var.get() == 2:
            Gui.configuration.SetBuildTypeRelease()

    @staticmethod
    def AskDirectory():
        # defining options for opening a directory
        dir_opt = options = {}
        options['initialdir'] = HomeDir()
        options['mustexist'] = True
        options['parent'] = Gui.top
        options['title'] = 'Directory containing cpp/external'
        dirname = tkFileDialog.askdirectory(**dir_opt)
        if len(dirname) > 0:
            Gui.configuration.SetCppExternalDir(dirname)

    @staticmethod
    def UpdateDirLabel():
        Gui.dirLabel.configure(text='Dir: %s' % Gui.configuration.GetCppExternalDir())

    @staticmethod
    def SetupCallBack():
        print "Setup.button selected"
        import os
        cmd = 'python ./setup.py -b %s -d %s' % (Gui.configuration.GetBuildType(), Gui.configuration.GetCppExternalDir())
        os.system(cmd)

    @staticmethod
    def ExecuteCallBack():
        print "Execute.button selected"
        import os
        cmd = 'python ./execute.py'
        os.system(cmd)

    @staticmethod
    def Show():
        Gui.configuration.ReadConfiguration()

        Gui.top = Tkinter.Tk()
        Gui.top.title("SGS performance test")

        label = Label(Gui.top, text="Select options and execute tasks", height=2)
        label.grid(row=0, sticky=W)

        # ########################################################

        Gui.var = IntVar()
        Gui.var.set(1 if Gui.configuration.IsBuildTypeDebug() else 2)
        R1 = Radiobutton(Gui.top, text="Debug Build", variable=Gui.var, value=1, height=2,command=Gui.BuildType)
        R1.grid(row=1, sticky=W)
        R2 = Radiobutton(Gui.top, text="Release Build", variable=Gui.var, value=2, height=2, command=Gui.BuildType)
        R2.grid(row=2, sticky=W)

        # ########################################################

        Gui.dirLabel = Label(Gui.top, text='Dir: ', bd=4)
        Gui.dirLabel.grid(row=3, sticky=W)
        Gui.UpdateDirLabel()

        dirButton = Tkinter.Button(Gui.top, text='Select dir containing cpp/external', bd=4, command=Gui.AskDirectory)
        dirButton.grid(row=4, sticky=W)

        # ########################################################

        def SetupCallBack():
           print "Setup"
        setup = Tkinter.Button(Gui.top, text ="Setup", bd=4, command = Gui.SetupCallBack)
        setup.grid(row=5, sticky=W)

        def ExecuteCallBack():
           print "Execute"
        execute = Tkinter.Button(Gui.top, text ="Execute", bd=4, command = Gui.ExecuteCallBack)
        execute.grid(row=6, sticky=W)

        # new line
        print ''
        Gui.top.mainloop()

# ###########################################################
# main
AssertLinuxPlatform()
Gui.Show()