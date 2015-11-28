#!/usr/bin/python

# http://code.activestate.com/recipes/435875-a-simple-non-recursive-directory-walker/
import os, os.path

directory = "./.."
directories = []
directoryNames = []
for name in os.listdir(directory):
        fullpath = os.path.join(directory,name)
        print 'name    : ',name
        print 'fullpath: ',fullpath
        if os.path.isfile(fullpath):
            print fullpath                # That's a file. Do something with it.
        elif os.path.isdir(fullpath):
            directories.append(fullpath)  # It's a directory, store it.
            directoryNames.append(name)

print directories
print directoryNames          