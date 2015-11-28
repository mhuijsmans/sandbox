#!/usr/bin/python

# ref: http://stackoverflow.com/questions/973473/getting-a-list-of-all-subdirectories-in-the-current-directory
import os

# example works, but I have no clue yet, when I would use this code.
print "root prints out directories only from what you specified"
print "dirs prints out sub-directories from root"
print "files prints out all files from root and directories"
print "*" * 20
for root, dirs, files in os.walk("./.."):
    print root
    print dirs
    print files