#!/usr/bin/python

# used source: http://preshing.com/20110920/the-python-with-statement-by-example/

# Exmaple from the above link that shows more capabilities than used in the test below
class Saved():
    def __init__(self, cr):
        self.cr = cr
    def __enter__(self):
        self.cr.save()
        return self.cr
    def __exit__(self, type, value, traceback):
        self.cr.restore()

class WorkingDirectoryChangedTo():
    def __init__(self, newDir):
        self.newDir = newDir
    def __enter__(self):
        print '__enter__ ', self.newDir
    def __exit__(self, type, value, traceback):
        print '__exit__ ', self.newDir

class Test:

    def __init__(self):
        pass

    def Test1(self):
        with WorkingDirectoryChangedTo("newDir"):
            print "Inside saved"

# ###########################
# Main Application
# ###########################

test = Test()
test.Test1()
