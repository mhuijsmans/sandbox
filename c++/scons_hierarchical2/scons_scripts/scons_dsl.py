import os
Import('env')

env.Debug('### Adding Scons DSL')

# ############################################################
# class to hold data about a specific project

class BuildProject:
    def __init__(self, env, name, buildDir):
        import posixpath, re
        self.env_ = env
        self.env_.Debug('BuildProject.ctor()')
        self.name_ = name
        self.nameShort_ = name.split('.')[0]
        self.nameShortUppercase = self.nameShort_.upper()
        self.buildtarget = None
        self.buildTargetName = None
        self.nameApplication = None
        # dictionary provides easy key match; list does not
        # BUT, with dependencies, order matters. See readme.
        self.dependsOn = []
        self.dependsOnDict = {}
        self.dependsOnInclude = []
        # BUILD_DIR == variant_dir
        # structure must be in sync with choice in visitBuildDirectories
        self.buildDir = buildDir
        self.targetDir = posixpath.normpath( buildDir+'/../..' )
        self.baseDir = posixpath.normpath( buildDir+'/../../..' )
        self.basebaseDir = posixpath.normpath( buildDir+'/../../../..' )
        self.sourceDir = posixpath.normpath( buildDir+'/../../../source' )
        self.includeDir = posixpath.normpath( buildDir+'/../../../include')
        # no testdir. Not needed sofar
        self.generatedSourceDir = self.targetDir+'/generated_source'
        #
        self.isTest = os.path.basename(self.buildDir) == 'test'
        self.isComponent = os.path.basename(self.basebaseDir) == 'components'
        self.isDynamicComponent = False
        self.isStaticComponent = False
        self.isApplication = os.path.basename(self.basebaseDir) == 'applications'
        self.isRpm = os.path.basename(self.basebaseDir) == 'rpms'
        self.isContainingGeneratedCode = False
        if self.isComponent and not self.isTest:
            self.NAME_ = self.nameShortUppercase
        else:
            self.NAME_ = ''
        if env.IsDebug():
            print ". name                : ",self.name_
            print ". nameShortUppercase  : ",self.nameShortUppercase
            print ". NAME                : ",self.NAME_
            print ". BUILD_DIR           : ",self.buildDir
            print ". BUILD_DIR           : ",self.buildDir
            print ". TARGET_DIR          : ",self.targetDir
            print ". BASE_DIR            : ",self.baseDir
            print ". BASEBASE_DIR        : ",self.basebaseDir
            print ". SOURCE_DIR          : ",self.sourceDir
            print ". INCLUDE_DIR         : ",self.includeDir
            print ". GENERATED_SOURCE_DIR: ",self.generatedSourceDir
            print ". isTest              : ",self.isTest
            print ". isComponent         : ",self.isComponent
            print ". isApplication       : ",self.isApplication
            print ". isRpm               : ",self.isRpm
        # assert valid name
        assert name.endswith('.source') or name.endswith('.test'), 'Name must end with .source ot .test, found: %s' %  name
        # assert valid base dir
        allowedBaseBaseDirNames =  ['components', 'applications','rpms']
        validBaseDirName = set(re.findall(r'\b(%s)\b' % '|'.join(allowedBaseBaseDirNames),
            os.path.basename(self.basebaseDir)))
        assert validBaseDirName, 'Base directory name must be called: %s' %  ','.join(allowedBaseDirNames)
        # assert valid SConscript dir
        allowedBaseDirNames =  ['source','test']
        validBaseDirName = set(re.findall(r'\b(%s)\b' % '|'.join(allowedBaseDirNames),
            os.path.basename(self.buildDir)))
        assert validBaseDirName, 'SConscript directory name must be called: %s' %  ','.join(allowedBaseDirNames)

    def Env(self):
        return self.env_

    # name in uppercase, without .
    def NameShortUppercase(self):
        # name variable can not be same as method
        return self.nameShortUppercase

    def Name(self):
        # name variable can not be same as method
        return self.name_

    def NameShort(self):
        # name variable can not be same as method
        return self.nameShort_

    def NameSourceProject(self):
        # name variable can not be same as method
        assert self.isTest, "Method only allowed for test project"
        return self.nameShort_+'.source'

    def NAME(self):
        return self.NAME_

    # component(y/n) is derived from basebasedir
    def IsComponent(self):
        return self.isComponent

    # application(y/n) is derived from basedir
    def IsApplication(self):
        return self.isApplication

    def IsComponentOrApplication(self):
        return self.IsComponent() or self.IsApplication()

    # test(y/n) is derived from basename
    def IsTest(self):
        assert self.IsApplication() or self.IsComponent(), 'Test is only supported for component / application'
        return self.isTest

    # rpm(y/n) is derived from basedir
    def IsRpm(self):
        return self.isRpm

    # basebase dir the the directory that contains the component/application
    # Examples: <rootdir>/components, <rootdir>/test/components
    def BaseBaseDir(self):
        return self.basebaseDir

    def BuildDir(self):
        return self.buildDir

    def SourceDir(self):
        return self.sourceDir

    def IncludeDir(self):
        return self.includeDir

    def IfIncludeDirExists(self):
        return self.DirExists(self.IncludeDir())

    def DirExists(self, dir):
        return os.path.isdir(dir) and os.path.exists(dir)

    def TargetDir(self):
        return self.targetDir

    def GeneratedSourceDir(self):
        return self.generatedSourceDir

    def DependsOn(self, name):
        if not name in self.dependsOnDict:
            self.dependsOn.append(name)
            self.dependsOnDict[name] = name
            return True
        return False

    def DependsOnInclude(self, name):
        self.dependsOnInclude.append(name)

    def DependenciesClone(self):
        return list(self.dependsOn)

    def IncludeDependenciesClone(self):
        return list(self.dependsOnInclude)

    def IsKnownDependency(self,name):
        return name in self.dependsOnDict

    def SetNameApplication(self, name):
        assert self.IsApplication(), 'This method is only allowed for an application project'
        assert self.nameApplication is None, 'This method can only be called once'
        self.env_.Debug('BuildProject.SetNameApplication project: %s, name: %s' % (self.name_, name))
        self.nameApplication = name

    def SetContainsGeneratedCode(self):
        self.env_.Debug('BuildProject.SetContainsGeneratedCode project: %s' % (self.name_))
        self.isContainingGeneratedCode = True

    def IsContainingGeneratedCode(self):
        return self.isContainingGeneratedCode

    def SetStaticComponent(self):
        self.env_.Debug('BuildProject.SetStaticComponent')
        assert self.isComponent == True, 'Property can only be set for component'
        assert self.isTest == False, 'Property can not be set for test'
        assert self.isStaticComponent == False, 'Property already set; can be set only once'
        assert self.isDynamicComponent == False, 'Conflict with property isDynamicComponent, which has been set'
        self.isStaticComponent = True

    def SetDynamicComponent(self):
        self.env_.Debug('BuildProject.SetDynamicComponent')
        assert self.isComponent == True, 'Property can only be set for component'
        assert self.isTest == False, 'Property can not be set for test'
        assert self.isDynamicComponent == False, 'Property already set; can be set only once'
        assert self.isStaticComponent == False, 'Conflict with property isStaticComponent, which has been set'
        self.isDynamicComponent = True

    def IsDynamicComponent(self):
        assert self.isComponent == True, 'Method only allowed for Component'
        assert self.isTest == False, 'Method not allowed test project of a component'
        return self.isDynamicComponent

    def IsStaticComponent(self):
        assert self.isComponent == True, 'Method only allowed for Component'
        assert self.isTest == False, 'Method not allowed test project of a component'
        return self.isStaticComponent

    # a component can be header only (i.e. only include folder)
    def IsHeaderOnlyComponent(self):
        assert self.isComponent == True, 'Method only allowed for Component'
        assert self.isTest == False, 'Method not allowed test project of a component'
        return self.isStaticComponent ==False and self.isDynamicComponent == False

    def GetNativeNameLibrary(self):
        assert self.Env().IsOSLinux(), 'Method only implemented for Linux'
        if self.IsDynamicComponent():
            return 'lib%s.so' % self.GetBuildTargetName()
        if self.IsStaticComponent():
            return 'lib%s.a' % self.GetBuildTargetName()
        assert False, 'Error: property static/dynamic component not set for project: %s' % self.Name()

    def GetAbsolutePathNativeLibrary(self):
        return self.BuildDir() + '/' + self.GetNativeNameLibrary()

    def GetFileApplicationExecutable(self):
        self.env_.Debug('BuildProject.GetFileApplicationExecutable project: %s' % (self.name_))
        return self.BuildDir() +'/'+ self.nameApplication

    # buildTarget is the return value of the opt.Program(..), opt.SharedLibrary(..), opt.StaticLibrary(..)
    # buildTarget is needed for certain scons operations, such opt.Depends(..,..).
    def SetBuildTarget(self,target):
        assert self.buildtarget is None, 'BuildTarget already set; can be set only once'
        self.buildtarget = target

    def GetBuildTarget(self):
        return self.buildtarget

    # buildTargetName is the name specified for the library, program or rpm
    def SetBuildTargetName(self, targetName):
        assert self.buildTargetName is None, 'BuildTargetName already set; can be set only once'
        self.buildTargetName = targetName

    def GetBuildTargetName(self):
        assert self.buildTargetName is not None, 'BuildTargetName not set'
        return self.buildTargetName

    def IsBuildTargetNameSet(self):
        return self.buildTargetName is not None

    # return the name to be used as scons targetname,
    def GetSconsTargetname(self):
        if not self.IsTest():
            # for component/application, the use if free to specify the name of the
            # library of executable.
            return self.NameShort()
        else:
            return self.GetBuildTargetName()

    def AddIncludeAndSourceDirToCompileIncludePath(self):
        if self.IsComponent() or self.IsApplication():
            if self.DirExists(self.IncludeDir()):
                self.env_.Append(CPPPATH = [self.IncludeDir()])
            # Source dir can also contain headers
            if self.DirExists(self.SourceDir()):
                self.env_.Append(CPPPATH = [self.SourceDir()])

# ############################################################
# Support functions

# Following dictionaries are maintained
# Global
# > BuildProjects(),            key = project name, e.g. app1.source
# > ComponentBuildProjects(),   key = project name short, uppercase, e.g. APP1
# > ApplicationBuildProjects(), key = project name short, uppercase, e.g. APP1
# BuildProject
# > Dependencies, key = project name short, uppercase
# > DependenciesInclude, key = project name short, uppercase

# returns BuildProject object
def buildProject(env):
  return env['BUILDPROJECT']
env.AddMethod(buildProject, "BuildProject");

# returns dictionary with all BuildProject
def buildProjects(env):
    if not 'BUILDPROJECTS' in env:
        env['BUILDPROJECTS'] = {}
    return env['BUILDPROJECTS']
env.AddMethod(buildProjects, "BuildProjects");

# returns BuildProject for given name
def buildProjectExists(env, name):
    return name in env.BuildProjects()
env.AddMethod(buildProjectExists, "BuildProjectExists");

# returns BuildProject for given name
def buildProjectByName(env, name):
    return env.BuildProjects()[name]
env.AddMethod(buildProjectByName, "BuildProjectByName");

# returns dictionary with Comnponent BuildProjects
def componentBuildProjects(env):
    if not 'COMPONENTBUILDPROJECTS' in env:
        env['COMPONENTBUILDPROJECTS'] = {}
    return env['COMPONENTBUILDPROJECTS']
env.AddMethod(componentBuildProjects, "ComponentBuildProjects");

# returns dictionary with Comnponent BuildProjects
def isComponentBuildProject(env, name):
    return name in env.ComponentBuildProjects()
env.AddMethod(isComponentBuildProject, "IsComponentBuildProject");

# returns Application BuildProject
def findComponentBuildProject(env, name):
    return env.ComponentBuildProjects()[name]
env.AddMethod(findComponentBuildProject, "FindComponentBuildProject");

# returns dictionary with Application BuildProjects
def applicationBuildProjects(env):
    if not 'APPLICATIONBUILDPROJECTS' in env:
        env['APPLICATIONBUILDPROJECTS'] = {}
    return env['APPLICATIONBUILDPROJECTS']
env.AddMethod(applicationBuildProjects, "ApplicationBuildProjects");

# returns Application BuildProject
def findApplicationBuildProject(env, name):
    return env.ApplicationBuildProjects()[name]
env.AddMethod(findApplicationBuildProject, "FindApplicationBuildProject");

# returns parent evironment
def parent(env):
  return env['PARENT']
env.AddMethod(parent, "Parent");

def assertIsRoot(env):
  assert not 'PARENT' in env, 'Method is only allowed on root Environment'
env.AddMethod(assertIsRoot, "AssertIsRoot")

# returns /target/[build|debug|profile]/[source|test]
def buildDir(env):
  return env.BuildProject().BuildDir()
env.AddMethod(buildDir, "BuildDir");

# returns /target folder
def targetDir(env):
  return env.BuildProject().TargetDir()
env.AddMethod(targetDir, "TargetDir");

# returns /source folder
def sourceDir(env):
  return env.BuildProject().SourceDir()
env.AddMethod(sourceDir, "SourceDir");

# returns /target/generated_source folder
def generatedSourceDir(env):
  return env.BuildProject().GeneratedSourceDir()
env.AddMethod(generatedSourceDir, "GeneratedSourceDir");

# ############################################################
# Create a clone
def createClone(env, nameCompOrApp):
    env.AssertIsRoot()
    assert nameCompOrApp.endswith('.source') or nameCompOrApp.endswith('.test'), \
                        'CreateClone: name must end with .source or .test'
    env.Debug('# Adding build: %s' % nameCompOrApp)
    env['BUILD_ROOT'] = Dir('#')   # dir when SConstruct resides
    opt = env.Clone()
    buildProject = BuildProject(opt, nameCompOrApp, Dir('.').abspath)
    assert not buildProject.Name() in env.BuildProjects(), \
                        'CreateClone: name not unique: %s' %    nameCompOrApp
    env.BuildProjects()[buildProject.Name()] = buildProject
    #
    # Note that the 2 Component and Application dictionary have uppercase keys
    if buildProject.IsComponent() and not buildProject.IsTest():
        env.ComponentBuildProjects()[buildProject.NAME()] = buildProject
    if buildProject.IsApplication() and not buildProject.IsTest():
        env.ApplicationBuildProjects()[buildProject.NameShortUppercase()] = buildProject
    opt['PARENT']=env
    opt['BUILDPROJECT'] = buildProject
    #
    # If clean set on commandline, delete target directory
    if opt.GetOption('clean'):
        env.DeleteTargetDirectory( buildProject.TargetDir() )
    return opt
env.AddMethod(createClone, 'CreateClone');

# ############################################################
# Add the Include path, library path and library
# to the given component/application
def dependsOn(env, name):
    # prepare for switch to drop uppercase naming rule
    name = name.upper()
    env.Debug(". DependsOn: %s" % name)
    buildProject = env.BuildProject()
    # here only dependency is registered.
    # In dependencyManagement, compiler & linker settings are added
    buildProject.DependsOn(name)
env.AddMethod(dependsOn, "DependsOn");

# ############################################################
# Add the include path of the given component to BuildProject
def dependsOnInclude(env, name):
    name = name.upper()
    env.Debug(". DependsOnInclude: %s" % name)
    buildProject = env.BuildProject()
    # here only dependency is registered.
    # In dependencyManagement, compiler & linker settings are added
    buildProject.DependsOnInclude(name)
env.AddMethod(dependsOnInclude, "DependsOnInclude");

# ############################################################
# Define an application
def buildProgram(opt, targetName, sourceList):
    opt.Debug(". program: %s" % targetName)
    opt.BuildProject().SetNameApplication(targetName)
    env = opt.Parent()
    target = opt.Program(target=targetName, source=sourceList)
    opt.BuildProject().SetBuildTarget(target)
    opt.BuildProject().AddIncludeAndSourceDirToCompileIncludePath()
    var = env.Alias(opt.BuildProject().GetSconsTargetname(), target)
    # next line appends (yes it does) var to the list all_apps.
    env.Alias('all_apps', var)
    env.Alias('all', var)
env.AddMethod(buildProgram, 'BuildProgram');

# ############################################################
# Define a static library
def buildStaticLibrary(opt, targetName, sourceList):
    opt.Debug(". staticLibrary: %s" % targetName)
    if opt.BuildProject().IfIncludeDirExists():
        opt.Append(CPPPATH = [
            opt.BuildProject().IncludeDir()
    ])
    opt.Append(CPPPATH = [opt.BuildProject().SourceDir()])
    lib = opt.StaticLibrary(target=targetName, source=sourceList)
    opt.BuildProject().SetBuildTarget(lib)
    opt.BuildProject().SetBuildTargetName(targetName)
    opt.BuildProject().SetStaticComponent()
    env = opt.Parent()
    var = env.Alias(opt.BuildProject().GetSconsTargetname(), lib)
    env.Alias('all_components', var)
    env.Alias('all', var)
    return lib
env.AddMethod(buildStaticLibrary, 'BuildStaticLibrary');

# ############################################################
# Define a shared library
def buildSharedLibrary(opt, targetName, sourceList):
    opt.Debug(". sharedLibrary: %s" % targetName)
    if opt.BuildProject().IfIncludeDirExists():
        opt.Append(CPPPATH = [
            opt.BuildProject().IncludeDir()
    ])
    opt.Append(CPPPATH = [opt.BuildProject().SourceDir()])
    opt.Append(CCFLAGS = '-fPIC')
    lib = opt.SharedLibrary(target=targetName, source=sourceList)
    opt.BuildProject().SetBuildTarget(lib)
    opt.BuildProject().SetBuildTargetName(targetName)
    opt.BuildProject().SetDynamicComponent()
    env = opt.Parent()
    var = env.Alias(opt.BuildProject().GetSconsTargetname(), lib)
    env.Alias('all_components', var)
    env.Alias('all', var)
    return lib
env.AddMethod(buildSharedLibrary, 'BuildSharedLibrary');

# ############################################################
# Define a build project with given name and sourcelist
def buildTests(opt, targetName, sourceList):
    assert targetName.endswith('_tests'), 'Name of test target must end with _tests'
    opt.DependsOnGTest()
    opt.Debug( "Adding new test target: %s" % targetName)
    if (opt.BuildProject().IsComponent()):
        opt.DependsOn(opt.BuildProject().NameShort())
        # Test may want to access header files in source dir
        opt.Append(CPPPATH = [opt.BuildProject().SourceDir()])
    else: # application, add sourceDir and includeDir (if exist) to compile path
        opt.BuildProject().AddIncludeAndSourceDirToCompileIncludePath()
        # include all files from source, except main.cpp
        sourceList.append(
            opt.FilteredGlob(
                '%s/*.cpp' % opt.BuildProject().SourceDir(),
                ['main.cpp'])
            )
    name = opt.Program(target=targetName, source=sourceList)
    opt.BuildProject().SetBuildTarget(name)
    opt.BuildProject().SetBuildTargetName(targetName)
    # Add own dir to rpath which allows the creation of dynamic links
    # to shared libraries by dependency management
    opt.Append(LINKFLAGS='-Wl,-rpath,%s' % opt.BuildProject().BuildDir())
    #
    # abspath makes it cross platform win/linux
    command = name[0].abspath
    gtestfilter = ARGUMENTS.get('gtestfilter', '')
    if gtestfilter != '':
        command = '%s --gtest_filter=%s' % (command, gtestfilter)
    sconsTargetName = opt.BuildProject().GetSconsTargetname()
    var = opt.Alias(sconsTargetName, name, command)
    # AlwaysBuild ensures that the test are run when the target is selected
    opt.AlwaysBuild(var)
    #
    commandMemcheck = 'valgrind --leak-check=full %s' % command
    targetNameMemCheck = '%s_mc' % sconsTargetName
    opt.Debug( "Adding new memcheck test target: %s" % targetNameMemCheck)
    varMemCheck = opt.Alias(targetNameMemCheck, name, commandMemcheck)
    opt.AlwaysBuild(varMemCheck)
    #
    env = opt.Parent()
    env.Alias('all_tests', var)
    env.Alias('all_tests_mc', varMemCheck)
    env.Alias('all', var)
env.AddMethod(buildTests, "BuildTests");
