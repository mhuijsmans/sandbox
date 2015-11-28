import os.path
Import("env")

# ############################################################
# Global settings

class Settings:

    def __init__(self, env):
        self.env = env
        self.Debug('Settings.ctor()')
        self.gcov = False
        self.scmrevision = '0'
        self.buildtype = None
        self.variantDir = None

    def SetUseGcov(self):
        self.gcov = True

    def IsGcov(self):
        return self.gcov

    def SetScmRevision(self, value):
        self.scmrevision = value

    # return the SVN revision for checked-out code.
    def GetScmRevision(self):
        return self.scmrevision

    def SetBuildType(self, value):
        assert self.buildtype == None,'BuildType is already set'
        assert value == 'debug' or value == 'profile' or value == 'release','Invalid profiltype: %s' % value
        self.buildtype = value
        self.variantDir = 'target/%s' % value

    def GetBuildType(self):
        assert self.buildtype is not None,'BuildType is not set'
        return self.buildtype

    def GetVariantDir(self):
        assert self.buildtype is not None,'BuildType is not set'
        return self.variantDir

    def IsDebugBuild(self):
        assert self.buildtype is not None,'BuildType is not set'
        return self.buildtype == 'debug'

    def IsProfileBuild(self):
        assert self.buildtype is not None,'BuildType is not set'
        return self.buildtype == 'profile'

    def IsReleaseBuild(self):
        assert self.buildtype is not None,'BuildType is not set'
        return self.buildtype == 'release'

    def Debug(self,msg):
        self.env.Debug(msg)

# returns Settings
def settings(env):
    if not 'SETTINGS' in env:
        env['SETTINGS'] = Settings(env)
    return env['SETTINGS']
env.AddMethod(settings, "Settings")