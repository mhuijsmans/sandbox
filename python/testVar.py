#!/usr/bin/python

var1 = 'ab'
var2 = ['cd']
var3 = [['ef','gh'],['ij']]

print var1
print var2
print var3

def isString(var):
    f = isinstance(var, basestring)
    print 'Is %s a string: %s' % (var,f)

def isList(var):
    f = type(var) is list
    print 'Is %s a list: %s' % (var,f)

isString(var1)
isString(var2)
isString(var3)

isString(var2[0])

isList(var1)
isList(var2)
isList(var3)
isList(var2[0])
isList(var3[0])