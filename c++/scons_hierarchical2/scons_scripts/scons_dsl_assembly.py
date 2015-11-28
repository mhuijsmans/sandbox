Import('env')

env.Debug('### Adding Scons DSL Assembly Module')

# ############################################################
# Resolve and update dependencies for BuildProjects
def assembly(env):
    assert env.IsOSLinux(), 'Method only supported for Linux'
    assembly = Assembly(env)
    assembly.Assemble()
env.AddMethod(assembly, "Assembly");

# ############################################################
# Responsibilities
# - copy all libraries to target/lib/<build>

class Assembly:

    def __init__(self, env):
        self.env = env
        self.rootdir = Dir('.').abspath

    def Assemble(self):
        self.Debug('Assembly.Assemble ENTER')
        if self.env.GetOption('clean'):
            env.DeleteTargetDirectory(self.RootTargetDir() )
        else:
            self.CreateSconsInstallRulesForComponents()
        self.Debug('Assembly.Assemble LEAVE')

    def CreateSconsInstallRulesForComponents(self):
        buildLibDirInTarget = self.RootTargetDir()+'/lib/'+env.Settings().GetBuildType()
        buildStubLibDirInTarget = self.RootTargetDir()+'/stub'
        self.env.MkdirRecursive(buildLibDirInTarget)
        self.env.MkdirRecursive(buildStubLibDirInTarget)
        self.Debug('CreateSconsInstallRulesForComponents, buildLibDirInTarget=%s' % buildLibDirInTarget)
        self.Debug('CreateSconsInstallRulesForComponents, buildStubLibDirInTarget=%s' % buildStubLibDirInTarget)
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            self.Debug('CreateSconsInstallRulesForComponents, buildProject=%s' % buildProject.Name())
            if self.IsTobeInstalled(buildProject):
                self.Debug('. install to lib')
                self.env.Install(buildLibDirInTarget, buildProject.GetBuildTarget())
            elif self.IsStubTobeInstalled(buildProject):
                self.Debug('. install to STUB')
                self.env.Install(buildStubLibDirInTarget, buildProject.GetBuildTarget())
        self.env.Alias('install', buildLibDirInTarget)

    def RootTargetDir(self):
        return self.rootdir +'/target'

    def RootTestDir(self):
        return self.rootdir +'/test'

    def RootDir(self):
        return self.rootdir

    # A project is a RootComponent if the basebasedir is a dir in the rootdir
    # example: <rootdir>/components/<comp> is a RootComponents
    # example: <rootdir>/test/components/<comp> is not a RootComponents
    def IsRootComponent(self, buildProject):
        import os
        basebasebasedir = os.path.dirname(buildProject.BaseBaseDir())
        return basebasebasedir == self.RootDir()

    def IsTestComponent(self, buildProject):
        import os
        basebasebasedir = os.path.dirname(buildProject.BaseBaseDir())
        result = basebasebasedir == self.RootTestDir()
        return result

    # Determine if the BuildProject contains a target that needs to be installed
    def IsTobeInstalled(self, buildProject):
        result = buildProject.IsComponent() and buildProject.IsTest() == False and buildProject.IsHeaderOnlyComponent() == False and self.IsRootComponent(buildProject)
        return result

    # Determine if the BuildProject contains a target that needs to be installed
    def IsStubTobeInstalled(self, buildProject):
        result = buildProject.IsComponent() and buildProject.IsTest() == False and buildProject.IsHeaderOnlyComponent() == False and self.IsTestComponent(buildProject)
        return result

    def Debug(self, msg):
        self.env.Debug(msg)
