Import('env')
import os, posixpath

env.Debug('### Adding Scons DSL externals')

# ############################################################

class ExternalLibrary:
  
  def __init__(self, env, name, compileLinkPathfunction):
    self.name = name
    self.env = env
    self.isDynamicLibrary = False
    self.compileLinkPathfunction = compileLinkPathfunction
    self.dynamicLibraries = []
    
  def Name(self):
    return self.name
    
  def IsDynamicLibrary(self):
    return isDynamicLibrary
  
  def GetDynamicLibraries(self):
    return self.dynamicLibraries  
  
  # libraries can be value of list
  # if suffix is set, library will be lib<name>.<suffix>
  def AddDynamicLibraryData(self, path, libraries, suffix):
    assert path is not None, 'Error: Path is none'
    assert libraries is not None, 'Error: libraries is none'
    path = posixpath.normpath(path)
    assert os.path.exists(path) , 'Path doesn not exist %s' % path   
    assert os.path.isdir(path), 'Path doesn not dir %s' % path
    self.isDynamicLibrary = True
    for libname in libraries:
      libpath = '%s/lib%s.%s' % (path, libname, suffix)
      assert os.path.exists(libpath) , 'Library doesn not exist %s' % libpath   
      assert os.path.isfile(libpath), 'Library doesn not dir %s' % libpath
      self.env.Debug('Add external library %s to library-list' % libpath )
      self.dynamicLibraries.append(libpath)
    
  def AddSettingsToCompileLinkPath(self, optBuildProject):
    optBuildProject.Debug('Add external %s to BuildProject: %s' % (self.name, optBuildProject.BuildProject().Name()))
    env = optBuildProject.Parent()
    # Like in Sconscript, the called method uses the BuildProject Environment
    func = getattr(optBuildProject, self.compileLinkPathfunction, None)
    # next line is the actual call of the function
    func()
    
def externalLibraries(env):
    if not 'EXTERNALLIBRARIES' in env:
        env['EXTERNALLIBRARIES'] = {}
    return env['EXTERNALLIBRARIES']
env.AddMethod(externalLibraries, "ExternalLibraries");

def addExternalLibrary(env, externalLibrary):
    assert externalLibrary is not None, 'Error: externalLibrary is none'
    name = externalLibrary.Name()
    if not name in env.ExternalLibraries():
        name = externalLibrary.Name()
        env.Debug('Adding to ExternalLibraries dictionary %s' % name)
        env.ExternalLibraries()[name] = externalLibrary
env.AddMethod(addExternalLibrary, "AddExternalLibrary")   

def externalLibrary(env, name):
    assert name is not None, 'Error: name is none'
    assert name in env.ExternalLibraries(), 'Error: name=%s not found in external libraries' % name
    return env.ExternalLibraries()[name]
env.AddMethod(externalLibrary, "ExternalLibrary") 

def isExternalLibrary(env, name):
    return name in env.ExternalLibraries()
env.AddMethod(isExternalLibrary, "IsExternalLibrary")
    
# ############################################################
# returns dictionary with external components

def externalComponents(env):
    env.AssertIsRoot()
    if not 'EXTERNALCOMPONENT' in env:
        env['EXTERNALCOMPONENT'] = {}
    return env['EXTERNALCOMPONENT']
# env.AddMethod(externalComponents, "ExternalComponents");

def isExternalComponent(env, name):
    return name in env.ExternalLibraries()
env.AddMethod(isExternalComponent, "IsExternalComponent")

def addExternalComponentToBuildProject(opt, name):
    env.ExternalLibrary(name).AddSettingsToCompileLinkPath(opt)
env.AddMethod(addExternalComponentToBuildProject, "AddExternalComponentToBuildProject")

# ############################################################
# Add the include path, library path and library
def dependsOnGTestSettings(env):
    env.Debug(". Setting compiler/linker settings for: GTest")
    buildRootPath = Dir('#').abspath
    env.Append(CPPPATH = [
        buildRootPath+'/../external/gtest-1.7.0/include',
        buildRootPath+'/../external/gmock-1.7.0/include',
    ])
    env.Append(LIBPATH = [
        buildRootPath+'/../external/gmock-1.7.0/make'
    ])
    env.Append(LIBS = [
        env.File(buildRootPath+'/../external/gmock-1.7.0/make/gmock_main.a'),
            'pthread'
    ])
env.AddMethod(dependsOnGTestSettings, "DependsOnGTestSettings")

def dependsOnGTest(env):
    env.Debug(". DependsOn: GTest")
    externalLibrary = ExternalLibrary(env, 'GTEST','DependsOnGTestSettings')
    env.Parent().AddExternalLibrary(externalLibrary)
    env.BuildProject().DependsOn('GTEST')
env.AddMethod(dependsOnGTest, "DependsOnGTest")

# ############################################################
# Add the include path, library path and library
def dependsOnLibcurlSettings(env):
    env.Debug(". Setting compiler/linker settings for: libcurl")
    # for documentation
env.AddMethod(dependsOnLibcurlSettings, "DependsOnLibcurlSettings")

def dependsOnLibcurl(env):
    env.Debug(". DependsOn: Libcurl")
    externalLibrary = ExternalLibrary(env, 'LIBCURL','DependsOnLibcurlSettings')
    env.Parent().AddExternalLibrary(externalLibrary)
    env.BuildProject().DependsOn('LIBCURL')
env.AddMethod(dependsOnLibcurl, "DependsOnLibcurl")

# ############################################################
# Add the include path, library path and library
def dependsOnGcovSettings(env):
    env.Debug(". Setting compiler/linker settings for: gcov")
    env.Append(CCFLAGS = ['-fprofile-arcs', '-ftest-coverage'])
    env.Append(LIBS = ['gcov'])
env.AddMethod(dependsOnGcovSettings, "DependsOnGcovSettings");

def dependsOnGcov(env):
    env.Debug(". DependsOn: Gcov")
    externalLibrary = ExternalLibrary(env, 'GCOV','DependsOnGcovSettings')
    env.Parent().AddExternalLibrary(externalLibrary)    
    env.BuildProject().DependsOn('GCOV')
env.AddMethod(dependsOnGcov, "DependsOnGcov");

# ############################################################
# Add the include path, library path and library

def dependsOnBoostSettings(env):
    env.Debug(". Adding compiler/linker settings for: Boost")
    buildRootPath = Dir('#').abspath
     # For BOOST Dynamic linking is used
    env.Append(CCFLAGS = ['-DBOOST_LOG_DYN_LINK'])
    # TODO: next line needs to be updated after external has been moved to cpp
    env['BOOST_BASE_DIR'] =  posixpath.normpath( buildRootPath+'/../external/boost_1_56_0')
    env['BOOST_LINK_PATH'] = posixpath.normpath( buildRootPath+'/../external/boost_1_56_0/lib/linux')
    env.Append(CPPPATH = [
        env['BOOST_BASE_DIR']
    ])
    libpath = env['BOOST_BASE_DIR']+'/lib/linux'
    env.Append(LIBPATH = [ libpath ])
    libs = ['boost_chrono',
        'boost_date_time',
        'boost_filesystem',
        'boost_log',
        'boost_log_setup',
        'boost_program_options',
        'boost_regex',
        'boost_system',
        'boost_thread' ]
    env.Append(LIBS = libs)
    env.Append(LINKFLAGS='-Wl,-rpath,$BOOST_LINK_PATH')
    #
    env.Parent().ExternalLibrary('BOOST').AddDynamicLibraryData(libpath, libs, 'so.1.56.0')
env.AddMethod(dependsOnBoostSettings, "DependsOnBoostSettings")

def dependsOnBoost(env):
    env.Debug(". DependsOn: Boost")
    externalLibrary = ExternalLibrary(env, 'BOOST','DependsOnBoostSettings')
    env.Parent().AddExternalLibrary(externalLibrary)        
    env.BuildProject().DependsOn('BOOST')
env.AddMethod(dependsOnBoost, "DependsOnBoost")


