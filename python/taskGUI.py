#!/usr/bin/python

import Tkinter, Tkconstants, tkFileDialog
from  Tkinter import Label, Button, Radiobutton, W, IntVar
import tkMessageBox
import glob

# ##########################################################
# Generic functions
def HomeDir():
    from os.path import expanduser
    return expanduser("~")

def AssertLinuxPlatform():
    from sys import platform as _platform
    assert _platform == "linux" or  _platform == "linux2", "This application is developed for Linux, platform= %s" % _platform

# ##########################################################
# Task supported by GUI
class Task:

    def __init__(self, name, command, executeDir=None):
        self.name = name
        self.command = command
        self.executeDir = executeDir

    def GetName(self):
        return self.name

    def GetCommand(self):
        if self.executeDir is None:
            return self.command
        else:
            return 'cd %s;%s' % (self.executeDir , self.command)

    def SetButton(self, button):
        self.button = button

    def GetButton(self):
        return self.button

    def GetExecuteDir(self):
        return self.executeDir

# ##########################################################
# Constructing the GUI

class Gui:

    top = None
    buttons = []

    def __init__(self):
        pass

    @staticmethod
    def ExecuteCommand(task):
        print '-------------------------------------------------------------------------------------'
        print 'Command %s' % task.GetCommand()
        import os
        os.system(task.GetCommand())

    @staticmethod
    def Show(taskList):

        Gui.top = Tkinter.Tk()
        Gui.top.title("Task GUI")

        label = Label(Gui.top, text="Available tasks", height=2)
        label.grid(row=0, sticky=W)

        # ########################################################

        rowNr = 5
        for task in taskList:
            # Note that in order to pass it needs to be copied (the task=task after the lambda)
            task.SetButton(Tkinter.Button(Gui.top, text =task.GetName(), bd=4, command=lambda task=task: Gui.ExecuteCommand(task)))
            task.GetButton().grid(row=rowNr, sticky=W)
            rowNr += 5

        # new line
        print ''
        Gui.top.mainloop()

# ###########################################################
# main
AssertLinuxPlatform()

softwareDir = '/home/userx/blabla'
cppDir = softwareDir+'/cpp'
javaDir = softwareDir+'/java'
ssaDir = javaDir + '/ssa'

gnomeTerminal180Plain = 'gnome-terminal --geometry=180x20'
gnomeTerminal120 = 'gnome-terminal --geometry=120x20 --working-directory'
gnomeTerminal180 = 'gnome-terminal --geometry=180x20 --working-directory'

xyz1_rpm = glob.glob(cppDir+'/rpms/xyz1/target/debug/source/RPMS/x86_64/xyz1-0.*.rpm')[0]
yum_install_xyz1 = 'sudo yum install -y %s' % xyz1_rpm
yum_remove_xyz1  = 'sudo yum remove -y xyz1'

xyz2_rpm = glob.glob(cppDir+'/test/rpms/xyz2/target/debug/source/RPMS/x86_64/xyz2-0.*.rpm')[0]
yum_install_xyz2 = 'sudo yum install -y %s' % xyz2_rpm
yum_remove_xyz2 = 'sudo yum remove -y xyz2'

ssa_rpm = glob.glob(ssaDir+'/abx-rpm/target/rpm/abx-rpm/RPMS/noarch/abx-rpm-*.rpm')[0]
yum_install_ssa = 'sudo yum install -y %s' % ssa_rpm
yum_remove_ssa = 'sudo yum remove -y abx-rpm'
cp_ssa_war = 'sudo cp /opt/xyz1/webapps/abx.war /opt/tomcat/apache-tomcat-8.0.20/webapps/xyz1.war'

import multiprocessing
nrOfCpu = multiprocessing.cpu_count()
nrOfSconsJobs = nrOfCpu * 2

taskList = []
taskList.append(Task('ls cpp  ','ls', cppDir))
taskList.append(Task('ls java ','ls', javaDir))
taskList.append(Task('systemctl status all','sudo systemctl status tomcat xyz2 &'))
taskList.append(Task('ls /opt','ls -ls /opt'))
taskList.append(Task('ls /opt/xyz1','sudo ls -ls /opt/xyz1'))
taskList.append(Task('tail log messages','%s -e "sudo tail -f /var/log/messages"' % gnomeTerminal180Plain))
taskList.append(Task('cpp window','%s=%s -x bash -c "bash" &' % (gnomeTerminal120, cppDir)))
taskList.append(Task('java window','%s=%s -x bash -c "bash" &' % (gnomeTerminal120, javaDir)))
taskList.append(Task('rpm java','%s=%s -x bash -c "mvn package; sleep 30" &' % (gnomeTerminal120, ssaDir)))
taskList.append(Task('rpm cpp','%s=%s -x bash -c "scons all_rpms -j %s; sleep 30" &' % (gnomeTerminal120, cppDir, nrOfSconsJobs)))
taskList.append(Task('cpp build','%s=%s -x bash -c "scons -j %s; sleep 30" &' % (gnomeTerminal180, cppDir, nrOfSconsJobs)))
taskList.append(Task('cpp tests','%s=%s -x bash -c "scons all_tests; sleep 30" &' % (gnomeTerminal180, cppDir)))
taskList.append(Task('cpp all_rpms','%s=%s -x bash -c "scons all_rpms; sleep 30" &' % (gnomeTerminal180, cppDir)))

taskList.append(Task('install xyz1_rpm','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, cppDir, yum_install_xyz1)))
taskList.append(Task('remove xyz1_rpm','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, cppDir, yum_remove_xyz1)))
taskList.append(Task('install xyz2_rpm','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, cppDir, yum_install_xyz2)))
taskList.append(Task('remove xyz2_rpm','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, cppDir, yum_remove_xyz2)))
taskList.append(Task('install ssa_rpm','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, javaDir, yum_install_ssa)))
taskList.append(Task('remove ssa_rpm','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, javaDir, yum_remove_ssa)))
taskList.append(Task('copy war','%s=%s -x bash -c "%s; sleep 30" &' % (gnomeTerminal180, javaDir, cp_ssa_war)))

Gui.Show(taskList)