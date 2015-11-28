#!/usr/bin/python

import os
	
file = 'touch.test'
	
# next line doesn't change time if the file exists	
open(file, 'a').close()
lastModifiedTime = os.path.getmtime(file)
print 'Touch: %s, lastModifiedTime: %s' % (file,lastModifiedTime)

os.utime(file,None)
lastModifiedTime = os.path.getmtime(file)
print 'Touch: %s, lastModifiedTime: %s' % (file,lastModifiedTime)