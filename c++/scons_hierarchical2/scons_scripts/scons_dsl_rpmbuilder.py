Import('env')

# returns BuildProject object
def rpmBuilder(env):
  return env['RPMBUILDER']
env.AddMethod(rpmBuilder, "RpmBuilder");

class RpmBuilder:

  def __init__(self, env, rpmName, nameRpmSpec, CopyDataFunction):
    self.env = env
    self.Debug('RpmBuilder rpmName: %s nameRpmSpec: %s' % (rpmName, nameRpmSpec))
    self.rpmName = rpmName
    self.nameRpmSpec = nameRpmSpec
    self.nameRpmRc = 'rpmrc'
    self.nameTarGz = 'temp.tar.gz'
    self.CopyDataFunction = CopyDataFunction
    #
    self.build_dir = self.env.BuildDir()
    self.source_dir = self.env.SourceDir()
    #
    self.fileTarGz = self.build_dir + '/'+self.nameTarGz
    self.fileRpmMacro = self.build_dir + '/'+self.nameRpmRc
    self.fileSrcRpmSpec = self.source_dir + '/'+self.nameRpmSpec
    self.fileDstRpmSpec = self.build_dir + '/SPECS/'+self.nameRpmSpec

  # Mandatory Method to be use in SConscript ()
  def SetProductNameAndVersion(self, name, version):
    import shutil
    assert not hasattr(self, 'productName'), 'Method SetProductNameAndVersion can be called only one'
    self.Debug('RpmBuilder name: %s, version: %s' % (name, version))
    self.productName = name
    self.productVersion = version
    #
    self.src_layout_dir = self.source_dir+'/layout'
    self.target_layout_base_dir = self.build_dir+'/layout'
    self.target_layout_dir = self.target_layout_base_dir+'/'+  self.productName+'-'+self.productVersion
    # scons copy command works with sources & targets
    # and they are confined to the SConscript scope.
    # That doesn't work when creating a rpm. So Pyhthon to the rescue.
    #
    # Copy layout template from src/layout to target/layout;
    # target/target must not exist
    self.Debug('. deleting directory: %s' % (self.target_layout_dir))
    shutil.rmtree(self.target_layout_dir, ignore_errors=True)
    self.Debug('. copying rpm layout\n.. from: %s\n.. to: %s' % (self.src_layout_dir, self.target_layout_dir))
    shutil.copytree(self.src_layout_dir, self.target_layout_dir)

  # Optional Method to be use in SConscript
  def CopyFile(self, srcFile, relativeLayoutDir):
    import shutil
    assert hasattr(self, 'productName'), 'Method SetProductNameAndVersion must be called before this method'
    targetFile = self.target_layout_dir+relativeLayoutDir
    self.Debug('. copying file: %s\n.. to directory: %s' % (srcFile, targetFile))
    shutil.copy(srcFile, targetFile)

  # Optional Method to be use in SConscript
  def  CopyApplication(self, nameExecutable, relativeLayoutDir):
    nameExecutable = nameExecutable.upper()
    buildProject = self.env.FindApplicationBuildProject(nameExecutable)
    srcFile = buildProject.GetFileApplicationExecutable()
    self.Debug('... copying file: %s to directory: %s' % (srcFile, relativeLayoutDir))
    self.CopyFile(srcFile, relativeLayoutDir)
    
  # Optional Method to be use in SConscript
  def CopyDynamicLibrariesReferencedByApplication(self, nameExecutable, relativeLayoutDir):
    import os
    nameExecutable = nameExecutable.upper()
    buildProject = self.env.FindApplicationBuildProject(nameExecutable)
    dependencies = buildProject.DependenciesClone()
    # check all dependencies. If dynamic library, copy the library
    for dependency in dependencies: 
      if self.env.IsExternalLibrary(dependency):
        externalLibrary = self.env.ExternalLibrary(dependency)
        soLibraries = externalLibrary.GetDynamicLibraries()
        for soLibrary in soLibraries:
          self.Debug('... copying file: %s to directory: %s' % (soLibrary, relativeLayoutDir))
          self.CopyFile(soLibrary, relativeLayoutDir)
      else:
        childProject = env.ComponentBuildProjects()[dependency]
        if childProject.IsDynamicComponent():
          soLibrary = childProject.GetAbsolutePathNativeLibrary()
          self.Debug('... copying file: %s to directory: %s' % (soLibrary, relativeLayoutDir))
          self.CopyFile(soLibrary, relativeLayoutDir)

  # Optional Method to be use in SConscript
  # This is a first / temporary solution. The external model shall be improved to allow a component
  # to give information about libraries.
  def CopyDynamicLibrariesReferencedByApplication1(self, nameExecutable, relativeLayoutDir):
      import os
      nameExecutable = nameExecutable.upper()
      buildProject = self.env.FindApplicationBuildProject(nameExecutable)
      # copy all dynamic libraries that are referenced by the executable
      self.Debug('. depending on libraries: %s' % buildProject.Env()['LIBS'])
      self.Debug('. depending on libraryPaths: %s' % buildProject.Env()['LIBPATH'])
      for librarypath in buildProject.Env()['LIBPATH']:
          self.Debug('.. inspecting libraryPath: %s' % librarypath)
          assert os.path.exists(librarypath), 'Librarypath not found: %s' % librarypath
          assert os.path.isdir(librarypath), 'Librarypath is not a directory: %s' % librarypath
          for library in buildProject.Env()['LIBS']:
              soLibraryName = 'lib%s.so' % library
              soLibrary = '%s/lib%s.so' % (librarypath, library)
              self.Debug('... inspecting if dynamiclibrary exists: %s' % soLibrary)
              if os.path.exists(soLibrary) and os.path.isfile(soLibrary):
                  for filename in os.listdir(librarypath):
                      if filename.startswith(soLibraryName) and len(filename) > len(soLibraryName):
                          soLibrary = '%s/%s' % (librarypath, filename)
                  self.Debug('... copying file: %s to directory: %s' % (soLibrary, relativeLayoutDir))
                  self.CopyFile(soLibrary, relativeLayoutDir)

  def CallCopyDataFunctionInSConscript(self):
    # Call the function in the rpm Sconscript to set data in target layout
    args = [self.env, self]
    rpmSpecificCopyDataFunction = ('%s' % self.CopyDataFunction).split()[1]
    self.Debug('Calling the RPM Sconscript copy data function: %s' % rpmSpecificCopyDataFunction)
    self.CopyDataFunction(*args)
    # Check rules CopyDataFunction must comply to
    assert hasattr(self, 'productName'), 'Method SetProductNameAndVersion must be called'

  # Create a tar.gz with the content of the RPM
  def CreateTarGzForLayout(self):
    self.Debug('CreateTarGzForLayout tar.gz: %s' % (self.nameTarGz))
    output_filename = self.fileTarGz
    source_dir = self.target_layout_base_dir
    #ref: http://stackoverflow.com/questions/2032403/how-to-create-full-compressed-tar-file-using-python
    self.Debug('CreateTarGzForLayout, output_filename: %s\n.. source_dir: %s' % (output_filename, source_dir))
    import os, tarfile
    with tarfile.open(output_filename, "w:gz") as tar:
      # "with .. as .." has an included tar.close()
      # see: http://stackoverflow.com/questions/2212643/python-recursive-folder-read
      for subSourceDir in self.GetSubFolderNonRecursive(source_dir):
        tar.add(subSourceDir, arcname=os.path.basename(subSourceDir))

  def CreateRPMDirectories(self):
    createRpmDirectories = 'mkdir -p %s/{RPMS,SRPMS,BUILD,SOURCES,SPECS,tmp}' % (self.build_dir)
    self.env.SystemCallWithResultCheck(createRpmDirectories,'Creation RPM directories failed')

  def CopyFilesToRPMDirectories(self):
    copyRmpSpec = 'cp %s/%s %s/SPECS' % (self.source_dir, self.nameRpmSpec, self.build_dir)
    self.env.SystemCallWithResultCheck(copyRmpSpec,'Copy rpm spec failed')
    copyTarGz = 'cp %s/%s %s/SOURCES' % (self.build_dir, self.nameTarGz, self.build_dir)
    self.env.SystemCallWithResultCheck(copyTarGz,'Creation of tar.gz failed')

  # The rpm.spec is a template
  def UpdateRpmSpec(self):
    filedata = None
    # Read
    filedata = self.env.ReadStringFromFile(self.fileDstRpmSpec)
    # Replace
    filedata = self.RpmSpecReplace(filedata,'[[name]]',self.productName)
    filedata = self.RpmSpecReplace(filedata,'[[version]]',self.productVersion)
    filedata = self.RpmSpecReplace(filedata,'[[release]]',self.env.Settings().GetScmRevision())
    # Write
    self.env.WriteStringToFile(self.fileDstRpmSpec,filedata)

  def CreateRPM(self):
    # rpm getting started: http://rpmbuildtut.wordpress.com/getting-started/
    # --quiet makes rpmbuild produce minimal output
    # --showrc shows used settings: http://www.rpm.org/max-rpm-snapshot/ch-rpmrc-file.html
    quiet = '--quiet' if not self.env.IsDebug() else '--showrc' if self.env.IsTrace() else ''
    define1 = '--define "_topdir %s"' % (self.build_dir)
    define2 = '--define "_tmppath %s/tmp"' % (self.build_dir)
    defines = define1 + ' ' + define2
    command = 'cd %s ; rpmbuild -bb -vv %s %s %s' \
                % (self.build_dir, quiet, defines, self.fileDstRpmSpec)
    self.env.SystemCallWithResultCheck(command,'RPM build failed')

  def GetSubFolderNonRecursive(self, directory):
    import os, os.path
    directories = []
    for name in os.listdir(directory):
      fullpath = os.path.join(directory,name)
      if os.path.isdir(fullpath):
        directories.append(fullpath)
    return directories

  def Debug(self, msg):
    self.env.Debug(msg)

  def RpmSpecReplace(self, filedata, textToFind, newText):
    assert filedata.find(textToFind)>=0, 'Error, can not find: %s in: %s' % (textToFind, self.nameRpmSpec)
    newFiledata = filedata.replace(textToFind, newText,1)
    assert newFiledata.find(textToFind)==-1, 'Error, multiple occurrences of: %s in: %s' % (textToFind, self.nameRpmSpec)
    return newFiledata

  # modifyTargets is called first and in order to allow the builder
  # to inform scons about new targets.
  @staticmethod
  def ModifyTargets(target, source, env):
    env.Debug('RpmBuilder, modifyTargets')
    rpmBuilder = env.RpmBuilder()
    target.append(env.SourceDir()+rpmBuilder.nameRpmSpec)
    return target, source

  # arguments are discussed here: http://www.scons.org/doc/1.1.0/HTML/scons-user/x3560.html
  # If rpm is built, this method is called by Scons
  @staticmethod
  def BuildRpm(target, source, env):
    env.Debug('RpmBuilder, BuildRpm')
    rpmBuilder = env.RpmBuilder()
    rpmBuilder.CallCopyDataFunctionInSConscript()
    rpmBuilder.CreateTarGzForLayout()
    rpmBuilder.CreateRPMDirectories()
    rpmBuilder.CopyFilesToRPMDirectories()
    rpmBuilder.UpdateRpmSpec()
    rpmBuilder.CreateRPM()

  # This is the method that is called by scons,
  # it odes the Scons pumbling
  # which next triggers the creation of the RPM
  # Scons will
  # > first (and always) call the emitter
  # > next the action, if the RPM needs to be build
  @staticmethod
  def buildRpm(opt, rpmName, nameRpmSpec, CopyDataFunction):
    opt.Debug('RpmBuilder.BuildRpm ENTER')
    assert not 'RPMBUILDER' in opt, 'Calling BuildRpm twice for same Environment is not supported'
    opt['RPMBUILDER'] = RpmBuilder(opt, rpmName, nameRpmSpec, CopyDataFunction)
    opt.Append(BUILDERS = {
      'RpmBuilder_builder' :
        opt.Builder(
          emitter = RpmBuilder.ModifyTargets,
          action = RpmBuilder.BuildRpm)
      })

    internal_builder = opt.RpmBuilder_builder(source=nameRpmSpec)
    internal_rpm = opt.Alias(rpmName, [internal_builder])
    opt.BuildProject().SetBuildTarget(internal_builder)

    env = opt.Parent()
    env.Alias('all_rpms', internal_rpm)
    env.Alias('all', internal_rpm)

# end of class RpmBuilder:

env.AddMethod(RpmBuilder.buildRpm, "BuildRpm");
