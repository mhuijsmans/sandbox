#!/usr/bin/python

# built-in functions: https://docs.python.org/2/library/functions.html

class C:
	def F1(self,msg):
		print msg

# Call function a string
c = C()
f = 'F1'
getattr(c,f)('hi')

 # ######################################################
 # pass as arguments: function + argument 

def perform( fun, *args ):
	fun( *args )

def Action( msg ):
	print msg

perform( Action, 'Hello' )

# ########################################################
# add method to class
# ref: http://dietbuddha.blogspot.nl/2012/12/python-metaprogramming-dynamically.html
def fn1(self):
	print 'A_Class.fn1(self)'
	# from ref it is not clear if next line is something special
	# my conclusion is no
	return id(self), self, type(self)

def fn2(self):
	print 'A_Class.fn2(self)'
	return self
 
# Traditional Class Definition
class A_Class(object):
  def method_a(self):
    return id(self), self, type(self)
 
instance = A_Class()
 
# Modify the class and add fn as a method
setattr(A_Class, 'method_b', fn1)
setattr(A_Class, 'method_c', fn2)
 
# Call the traditionally defined method
instance.method_a()
# Call the dynamically added method
instance.method_b()
instance.method_c().method_b()

instance1 = A_Class()
instance1.method_c().method_b()

# ################################################################
# add method to one objext
# ref: http://dietbuddha.blogspot.nl/2012/12/python-metaprogramming-dynamically.html
from types import MethodType

def fn3(self):
	print 'fn3(self)'
	return self

class B_Class(object):
  def fa(self):
    print 'B_Class.fa(self)'

instance2 = B_Class() 
instance3 = B_Class()
setattr(instance2, fn3.__name__, MethodType(fn3, instance2, type(instance2)))
 
# Calls the fn method attached to the instance
instance2.fn3().fa()
 
# Throws an exception
instance3.fn3().fa()