Import('env')

env.Debug('### Adding Scons DSL Dependency Management Module')

# ############################################################
# Resolve and update dependencies for BuildProjects
def resolveTransitiveDependencies(env):
    dependencyManagement = DependencyManagement(env)
    dependencyManagement.ResolveAndUpdateDependencies()
env.AddMethod(resolveTransitiveDependencies, "ResolveTransitiveDependencies");

# ############################################################
# Responsibilities
# - todo

class GraphNode:

    def __init__(self, env, name):
        self.env = env
        self.name = name
        self.dependencies = {}
        self.nrOfIncomingEdges = 0

    def GetName(self):
        return self.name

    def AddDependency(self, dst, dstNode):
        # For test application, there is a dependency on own library
        self.dependencies[dst] = dstNode

    def IncrIncomingEdges(self):
        self.nrOfIncomingEdges += 1
        print('GraphNode.IncrIncomingEdges %s  cntr: %d' % (self.name, self.nrOfIncomingEdges))

    def DecrIncomingEdgesDownstreamNode(self):
        for node in self.dependencies.itervalues():
            node.DecrIncomingEdges()

    def DecrIncomingEdges(self):
        assert self.nrOfIncomingEdges > 0, 'Implementation error: noOfIncomingEdges is 0'
        self.nrOfIncomingEdges -= 1
        print('GraphNode.DecrIncomingEdges %s  cntr: %d' % (self.name, self.nrOfIncomingEdges))

    def HasNoIncomingEdges(self):
        return self.nrOfIncomingEdges == 0

    def Dependencies(self):
        return self.dependencies.itervalues()

class DependencyGraph:

    def __init__(self,env):
        self.nodes = {}
        self.env = env

    def AddDependency(self, src, dst):
        self.Debug('DependencyGraph.AddDependency %s -> %s' % (src, dst))
        if not src in self.nodes:
            self.nodes[src] = GraphNode(self.env, src)
        srcNode = self.nodes[src]
        if not dst in self.nodes:
            self.nodes[dst] = GraphNode(self.env, dst)
        dstNode = self.nodes[dst]
        #update dependencies
        srcNode.AddDependency(dst, dstNode)
        dstNode.IncrIncomingEdges()

    # topology sorting (http://en.wikipedia.org/wiki/Topological_sorting)
    def CreateSortedList(self):
        self.Debug('DependencyGraph.CreateSortedList')
        self.sortedList = []
        while len(self.nodes) > 0:
            nodesWithoutIncomingEdges = self.GetNodesWithoutIncomingEdges()
            if len(nodesWithoutIncomingEdges) > 0:
                for node in nodesWithoutIncomingEdges:
                    self.Debug('DependencyGraph.CreateSortedList, adding to sorted list: %s' % node)
                    self.sortedList.append(node)
            else:
                if len(self.nodes) > 0:
                    srcNode = self.nodes.values()[0]
                    dstNode = srcNode.Dependencies()[0]
                    print('ERROR: Circular dependencyGraph, e.g. %s <-> %s' % (srcNode.GetName(),dstNode.GetName()))
                    import sys
                    sys.exit(1) # Or something that calls sys.exit()

    def GetNodesWithoutIncomingEdges(self):
        nodesWithoutIncomingEdges = []
        # first find all node without incoming edges
        for name, graphNode in self.nodes.iteritems():
            if graphNode.HasNoIncomingEdges():
                nodesWithoutIncomingEdges.append(name)
        # remove and decrement counter of the downstream graph
        for name in nodesWithoutIncomingEdges:
            self.nodes[name].DecrIncomingEdgesDownstreamNode()
            del self.nodes[name]
        return nodesWithoutIncomingEdges

    def SortDependencies(self, dependencies):
        depDict = {}
        for dep in dependencies:
            depDict[dep] = dep
        sortedDepList = []
        for sortedDep in self.sortedList:
            if sortedDep in depDict:
                sortedDepList.append(sortedDep)
                del depDict[sortedDep]
        # Copy remaining dependencies, e.g. library used only in test (e.g. GTEST).
        # Keep the order as found in input
        for dep in dependencies:
            if dep in depDict:
                sortedDepList.append(dep)
        self.Debug('DependencyGraph.SortDependencies input: %s' %  ('-'.join(dependencies)))
        self.Debug('DependencyGraph.SortDependencies output: %s' %  ('-'.join(sortedDepList)))
        assert len(dependencies) == len(sortedDepList), 'Error: difference is input - output list, must be same size'
        return sortedDepList

    def Debug(self, msg):
        self.env.Debug(msg)

# ############################################################
# Responsibilities
# - resolve transitieve dependencies for components, application, rpms
# - resolve dependencies from test to related source
# This should reside in own file, but that doesn't work.
# Python doesn not detect that DependencyManagement is a class. why ??

class DependencyManagement:

    def __init__(self, env):
        self.env = env

    def ResolveAndUpdateDependencies(self):
        self.Debug('DependencyManagement.ResolveAndUpdateDependencies')
        self.dependencyGraph = None
        import datetime as dt
        n1=dt.datetime.now()
        self.ResolveTransitiveDependenciesComponentsAndLibraries();
        self.CopyDependenciesfromSourceToTestProject()
        self.AddGlobalDependencies()
        self.SortDependenciesTopDown()
        self.AddCompilerAndLinkerSettingsForBuildProjects()
        if self.env.GetOption('clean') == False:
            self.CreateForComponentTestProjectsSymbolicLinksToDynamicLibraries()
        self.ShowBuildProjectCompileAndLinkSettings()
        self.ResolveTransitiveDependenciesRpms();
        n2=dt.datetime.now()
        self.Debug('DependencyManagement.ResolveAndUpdateDependencies completed in %d ms' % ((n2-n1).microseconds/1000))

    def ResolveTransitiveDependenciesComponentsAndLibraries(self):
        self.Debug('ResolveTransitiveDependenciesComponentsAndLibraries')
        iteration = 1
        ready = False
        # multiple iterations can be needed to find all dependencies
        # Because for example, if A depends on B, it is possible that A is first visisted,
        # next B and dependency C is added to B, than C also needs to be added to dependcyList of A.
        while ready == False:
            self.Debug('ResolveTransitiveDependenciesComponentsAndLibraries: iteration: %d' % iteration)
            changeCounter = self.ResolveTransitiveDependenciesComponentsAndLibraries_OneRun()
            ready = (changeCounter == 0)
            iteration += 1

    def ResolveTransitiveDependenciesComponentsAndLibraries_OneRun(self):
        self.Debug('ResolveTransitiveDependenciesComponentsAndLibraries_OneRun')
        changeCounter = 0
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            if buildProject.IsComponentOrApplication():
                self.Debug('ResolveTransitiveDependenciesComponentsAndLibraries: analysing buildProject: %s' % buildProject.Name())
                # the dictionary is changed in the iteration process.
                # .keys() returns a copy of the keys, so it is safe to iterate over these keys
                for dependency in buildProject.DependenciesClone():
                    changeCounter += self.AddTransitiveDependenciesUsingName(buildProject,dependency,'..')
        return changeCounter

    def AddTransitiveDependenciesUsingName(self, buildProject, nameDependency, indent):
        self.Debug('. is dependant on: %s' % nameDependency)
        # check all the dependencies of the component
        if self.env.IsExternalComponent(nameDependency):
            return 0
        elif self.env.IsComponentBuildProject(nameDependency):
            componentBuildProject = self.env.ComponentBuildProjects()[nameDependency]
            return self.AddTransitiveDependencies(buildProject, componentBuildProject, '..')
        else:
            env.FatalError('BuildProject: %s, unresolved dependency: %s' % (buildProject.Name(), nameDependency))

    def AddTransitiveDependencies(self, buildProject, componentBuildProject, indent):
        changeCounter = 0
        for dependency in componentBuildProject.DependenciesClone():
            if buildProject.IsKnownDependency(dependency):
                self.Debug('%s is dependant on: %s' % (indent,dependency))
            else:
                self.Debug('%s (new) is now dependant on: %s' % (indent,dependency))
                changeCounter += 1
                if self.env.IsExternalComponent(dependency):
                    buildProject.Env().DependsOn(dependency)
                else:
                    childProject = env.ComponentBuildProjects()[dependency]
                    buildProject.Env().DependsOn(childProject.NAME())
                    # recursive
                    changeCounter += self.AddTransitiveDependencies(buildProject,  childProject, indent+'.')
        return changeCounter

    def AddNewDependency(self, buildProject, nameNewDepedency, indent):
        self.Debug('%s (new) is now dependent on: %s' % (indent,nameNewDepedency))
        self.AddDependencyToBuildProject(buildProject,nameNewDepedency)
        changeCounter = 1
        if self.env.IsComponentBuildProject(nameNewDepedency):
            changeCounter += self.AddTransitiveDependencies(buildProject,  childProject, indent+'.')
        return changeCounter

    def ResolveTransitiveDependenciesRpms(self):
        self.Debug('ResolveTransitiveDependenciesRpms')
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            if buildProject.IsRpm():
                self.Debug('ResolveTransitiveDependenciesRpms: rpm buildProject: %s' % buildProject.Name())
                # the dictionary is changed in the iteration process.
                # .keys() returns a copy of the keys
                for dependency in buildProject.DependenciesClone():
                    self.Debug('. is dependent on: %s' % dependency)
                    assert dependency in env.ApplicationBuildProjects() , 'Error: rpm dependency is not an application: %s' % dependency
                    applicationBuildProject = env.ApplicationBuildProjects()[dependency]
                    self.Debug('. applicationBuildProject: %s' % applicationBuildProject.Name())
                    self.env.Depends(buildProject.GetBuildTarget(), applicationBuildProject.GetBuildTarget())

    def CopyDependenciesfromSourceToTestProject(self):
        self.Debug('CopyDependenciesfromSourceToTestProject')
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            if buildProject.IsComponentOrApplication() and  buildProject.IsTest():
                self.Debug('CopyDependenciesfromSourceToTestProject: test buildProject: %s' % buildProject.Name())
                #
                for dependency in buildProject.DependenciesClone():
                    self.Debug('. existing dependency: %s' % dependency)
                #
                nameSourceProject = buildProject.NameSourceProject()
                assert self.env.BuildProjectExists(nameSourceProject), '. no source project for test project %s' % buildProject.Name()
                sourceProject = self.env.BuildProjectByName(nameSourceProject)
                # the dictionary is changed in the iteration process.
                # .keys() returns a copy of the keys
                for dependency in sourceProject.DependenciesClone():
                    if buildProject.IsKnownDependency(dependency):
                        self.Debug('. in source project, existing dependency: %s' % dependency)
                    else:
                        self.Debug('. in source project, new dependency: %s' % dependency)
                        self.AddDependencyToBuildProject(buildProject,dependency)

    def AddGlobalDependencies(self):
        self.Debug('AddGlobalDependencies')
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            if buildProject.IsComponentOrApplication():
                if self.env.Settings().IsGcov():
                    self.Debug('BuildProject: %s' % buildProject.Name())
                    buildProject.Env().DependsOnGcov()

    # The linker need to find the libraries in the right order.
    # If not in correct order, you may find a "error: unresolved dependencies"
    # Inspiration taken from: http://en.wikipedia.org/wiki/Topological_sorting
    def SortDependenciesTopDown(self):
        self.Debug('SortDependenciesTopDown TODO')
        # First create a graph of dependencies
        self.dependencyGraph = DependencyGraph(self.env)
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            if buildProject.IsComponent() and not buildProject.IsTest():
                 for dependency in buildProject.DependenciesClone():
                    self.dependencyGraph.AddDependency(buildProject.NameShortUppercase(), dependency)
        # now create a sorted dependency graph
        self.dependencyGraph.CreateSortedList()

    def AddCompilerAndLinkerSettingsForBuildProjects(self):
        self.Debug('AddCompilerAndLinkerSettingsForBuildProjects')
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            #
            dependenciesClone = buildProject.DependenciesClone()
            # test, to ensure that dependencyGraph features can be disabled
            if self.dependencyGraph is not None:
                dependenciesClone = self.dependencyGraph.SortDependencies(dependenciesClone)
            for dependency in dependenciesClone:
                if self.env.IsComponentBuildProject(dependency):
                    dependentOnProject = self.env.FindComponentBuildProject(dependency)
                    # for generated code, the source and include files reside in target/generated_source
                    if dependentOnProject.IsContainingGeneratedCode():
                        buildProject.Env().Append(CPPPATH = [dependentOnProject.GeneratedSourceDir()])
                    # If project has a include directory, that is assumed to holds the public interface
                    # But if there is no include directory (e.g. because of generated code copied by hand)
                    # then the source directory is assumed to hold the public interface
                    if dependentOnProject.IfIncludeDirExists():
                        buildProject.Env().Append(CPPPATH = [dependentOnProject.IncludeDir()])
                    else:
                        buildProject.Env().Append(CPPPATH = [dependentOnProject.SourceDir()])
                    # A project may not product a target (library, program or rpm)
                    if dependentOnProject.IsBuildTargetNameSet():
                        buildProject.Env().Append(LIBPATH = [
                            dependentOnProject.BuildDir()
                        ])
                        buildProject.Env().Append(LIBS = [
                            dependentOnProject.GetBuildTargetName()
                        ])
                if self.env.IsExternalComponent(dependency):
                    buildProject.Env().AddExternalComponentToBuildProject(dependency)
            #
            for dependency in buildProject.IncludeDependenciesClone():
                dependentOnProject = self.env.FindComponentBuildProject(dependency)
                buildProject.Env().Append(CPPPATH = [dependentOnProject.IncludeDir()])

    def CreateForComponentTestProjectsSymbolicLinksToDynamicLibraries(self):
        self.Debug('CreateForComponentTestProjectsSymbolicLinksToDynamicLibraries')
        for buildProjectKey in self.env.BuildProjects():
            buildProject = self.env.BuildProjects()[buildProjectKey]
            if buildProject.IsComponentOrApplication() and  buildProject.IsTest():
                 for dependency in buildProject.DependenciesClone():
                    if self.env.IsComponentBuildProject(dependency):
                        dependentOnProject = self.env.FindComponentBuildProject(dependency)
                        self.Debug('BuildProject: %s, dependent on: %s' % (buildProject.Name(), dependentOnProject.Name()))
                        if dependentOnProject.IsDynamicComponent():
                            source = dependentOnProject.GetAbsolutePathNativeLibrary()
                            symLinkName = buildProject.BuildDir() +'/'+dependentOnProject.GetNativeNameLibrary()
                            self.env.CreateSymLink(source, symLinkName)

    def AddDependencyToBuildProject(self, buildProject, nameNewDependency):
        # Invoke the function that is normally also called from Sconscript
        buildProject.Env().DependsOn(nameNewDependency)

    def ShowBuildProjectCompileAndLinkSettings(self):
        self.Debug('ShowBuildProjectCompileAndLinkSettings')
        for buildProjectKey in self.env.BuildProjects():
            self.Debug("BuildProjectCompileAndLinkSettings, buildproject: %s" % buildProjectKey)
            buildProject = self.env.BuildProjects()[buildProjectKey]
            self.ProjectPrintLibraries(buildProject)

    def ProjectPrintLibraries(self, buildProject):
        if 'LIBS' in buildProject.Env():
            libs = buildProject.Env()['LIBS']
            self.Debug(". Libraries: %s" % (", ".join(str(x) for x in libs)) )

    def Debug(self, msg):
        self.env.Debug(msg)
