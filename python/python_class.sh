#!/usr/bin/python

class MyClass:
	i = 12345

	def __init__(self):
		self.debug('MyClass.ctor()')
		self.data = []

	def f(self):
		return 'hello world'

	def debug(self,msg):
		print msg

x = MyClass()  
print 'Calling class x.f returns: ',x.f()

dict = dict()
dict['x1'] = MyClass()  
dict['x2'] = MyClass()  
key='x3'
dict[key] = MyClass()  

def GetMyClass(dict,key):
	return dict[key]

print 'Calling class dict.x1.f returns: ',dict['x1'].f()
x2 = dict['x1']
print 'Calling class x2.f returns: ',x2.f()
print 'Calling class x3.f (1) returns: ',dict[key].f()
print 'Calling class x3.f (2) returns: ',GetMyClass(dict,key).f()

x2.debug('hi')
x2.debug('hi %s, %s' % ('string1', 'string2'))