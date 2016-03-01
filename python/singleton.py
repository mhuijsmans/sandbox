#!/usr/bin/python

class Helper:

    # State is kept in seperate class, so that there is a clear public interface
    class __Helper:

        def __init__(self, msg):
            self.msg = msg

        def NonStaticMethod(self):
            print '__Helper.nonStaticMethod'

    singleton = None

    @staticmethod
    def Init(msg):
        if Helper.singleton is None:
            Helper.singleton = Helper.__Helper(msg)

    @staticmethod
    def StaticMethod():
        print 'Helper.staticMethod'

    @staticmethod
    def StaticMethodUsingSingleton():
        # Call a non-static method
        Helper.singleton.NonStaticMethod()
        # Access a non-static variable
        print Helper.singleton.msg

class Test:

    def __init__(self):
        pass

    def TestStaticMethod(self):
        Helper.StaticMethod()

    def TestStaticSingletonMethod(self):
        Helper.Init('a-message')
        Helper.StaticMethodUsingSingleton()

# ###########################
# Main Application
# ###########################

test = Test()
test.TestStaticMethod()
test.TestStaticSingletonMethod()
