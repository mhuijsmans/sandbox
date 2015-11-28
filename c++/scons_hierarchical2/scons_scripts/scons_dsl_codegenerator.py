import os.path
Import("env")

# ############################################################
# Create an static library  with generated code

class CodeGenerator:
    def __init__(self, env, command):
        self.env = env
        self.command = command
        self.env.Debug('CodeGenerator.ctor()')

    # arguments are discussed here: http://www.scons.org/doc/1.1.0/HTML/scons-user/x3560.html
    def GenerateCode(self, command, nameDocumentForCodeGeneration, nameDummyTargetDocument):
        self.env.Debug('CodeGenerator.GenerateCode ENTER')
        documentUsedForCodeGeneration = self.env.SourceDir() + '/'+nameDocumentForCodeGeneration
        dummyTargetDocument = self.env.GeneratedSourceDir()+'/'+nameDummyTargetDocument
        #
        if self.env.IsDateFile1MoreRecentThanDateFile2(documentUsedForCodeGeneration, dummyTargetDocument):
            self.env.Debug('. source document code generation has changed OR no generated code yet, generating code')
            result = self.env.SystemCall(command +' '+documentUsedForCodeGeneration)
            if result == 0:
                self.env.Debug('. command completed successful')
                self.env.Touch(dummyTargetDocument)
            else:
                env.FatalError('code generation failed ')
                exit(1)
        else:
            self.env.Debug('. source document code generation has not changed, skipping code generation')

    def GenerateCodeCallback(self, target, source)          :
        self.env.Debug('modify_targets ENTER\n. target: %s\n. source: %s' % (map(str, target), map(str, source)) )
        nameDocumentForCodeGeneration = os.path.basename(source[0].abspath)
        nameDummyTargetDocument = os.path.basename(target[0].abspath)
        self.GenerateCode(self.command, nameDocumentForCodeGeneration, nameDummyTargetDocument)

# end of class CodeGenerator
# ##########################

# arguments are discussed here: http://www.scons.org/doc/1.1.0/HTML/scons-user/x3560.html
def codeGeneratorDummyAction(target, source, env):
    # See comment at opt.Builder( ).
    env.Debug('codeGeneratorBuilder.DummyAction')

# The emitter is called by scan during parsing of the SConscript
def codeGeneratorModifyTargets(target, source, env):
    env.Debug('codeGeneratorBuilder.ModifyTarget')
    env['CODEGENERATOR'].GenerateCodeCallback(target, source)
    return target, source

# ################################################
# Public interface: GetListOfGeneratedCppFiles(..)

def getListOfGeneratedCppFiles(opt, command, sourceFilesCodeGeneration):
  # Is user requested -c, do nothing
    if opt.GetOption('clean'):
        return [[],[]]

    opt.Append(CPPPATH = [
        opt.GeneratedSourceDir()
    ])
    opt.BuildProject().SetContainsGeneratedCode()

    # Ensure that generated source directory exist
    opt.SystemCall('mkdir -p %s &> /dev/null' % opt.BuildProject().GeneratedSourceDir())

    codeGeneratorBuilder = opt.Builder(
        # emitter is executed first
        emitter = codeGeneratorModifyTargets,
        # Scons calls action (I think) when scons decides to build a target.
        action = codeGeneratorDummyAction)
    opt.Append(BUILDERS = {'CodeGenerator' : codeGeneratorBuilder})

    usedSourceFilesCodeGeneration = []
    codeGenerators = []
    targetSeqNr = 1
    # the input is a list of items where an item can be
    # name.xsd
    # list: name.xsd + settingsForCodeGeneration
    for inputSourceFileCodeGeneration in sourceFilesCodeGeneration:
        if opt.IsOfTypeList(inputSourceFileCodeGeneration):
            sourceFileCodeGeneration = inputSourceFileCodeGeneration[0]
            settingsCodeGeneration = ' %s ' % inputSourceFileCodeGeneration[1]
        else:
            sourceFileCodeGeneration = inputSourceFileCodeGeneration
            settingsCodeGeneration = ''
        usedSourceFilesCodeGeneration.append(sourceFileCodeGeneration)
        # Create instance of the Code generator class
        opt['CODEGENERATOR'] =  CodeGenerator(opt, command+settingsCodeGeneration)
        #
        internal_comp_generate_code = opt.CodeGenerator(
            target=opt.GeneratedSourceDir()+'/dummy_target'+str(targetSeqNr),
            source=sourceFileCodeGeneration)
        codeGenerators.append(internal_comp_generate_code)
        targetSeqNr += 1

    # From tracing I have learned that next line is called after the codeGeneratorBuilder.emitter has completed
    generatedCppFiles = opt.GlobWithDebug(opt.GeneratedSourceDir()+'/*.cpp')
    return [generatedCppFiles, usedSourceFilesCodeGeneration]

env.AddMethod(getListOfGeneratedCppFiles, "GetListOfGeneratedCppFiles");

# ################################################
# Interface used by public interface.

def buildGeneratedLibrary(opt, targetName, command, inputDataCodeGeneration, staticLibbrary, sourceFiles=None):
    resultData = opt.GetListOfGeneratedCppFiles(command, inputDataCodeGeneration)
    generatedCppFiles = resultData[0]
    sourceFilesCodeGeneration = resultData[1]
    if sourceFiles is None:
        sourceFiles = []
    if staticLibbrary:
        internal_comp_library = opt.BuildStaticLibrary(targetName,
            [
                generatedCppFiles,
                sourceFiles
            ]
        )
    else:
        internal_comp_library = opt.BuildSharedLibrary(targetName,
            [
                generatedCppFiles,
                sourceFiles
            ]
        )
    # The alias ties it all together
    # By including sourceFilesCodeGeneration, I assume that scons is monitoring that file
    # for changes. But I am not 100% sure how scons works.
    # Also the code generated checks for changes source file(s).
    internal_comp_both = opt.Alias(targetName,
        [
            internal_comp_library,
            sourceFilesCodeGeneration
        ]
    )
    env = opt.Parent()
    env.Alias('all_components', internal_comp_both)
    env.Alias('all', internal_comp_both)

# ################################################
# Public interface: BuildGenerated...Library(..)
# > targetName: name of the output static/shared library
# > command: command to execute to generate data
#   the executed command will be: command [commandsettings] fileUsedForCodeGeneration
#   The command shall include opt.GeneratedSourceDir() as output directory where
#       the generated code will be wriiten to.
# > sourceFilesCodeGeneration: 2 options
# - list op files to use in code generation
# - list lists, where each sub-list element ::= [ nameFileUsedToGenerateCodeFrom, commandOptions]
#   this is useful when for each file different options are needed during code generation
# > sourceFiles: optional, additional sources included in the output library.

def buildGeneratedStaticLibrary(opt, targetName, command, sourceFilesCodeGeneration, sourceFiles=None):
    buildGeneratedLibrary(opt, targetName, command, sourceFilesCodeGeneration, True, sourceFiles)
env.AddMethod(buildGeneratedStaticLibrary, "BuildGeneratedStaticLibrary");

def buildGeneratedSharedLibrary(opt, targetName, command, sourceFilesCodeGeneration, sourceFiles=None):
    buildGeneratedLibrary(opt, targetName, command, sourceFilesCodeGeneration, False, sourceFiles)
env.AddMethod(buildGeneratedSharedLibrary, "BuildGeneratedSharedLibrary");
