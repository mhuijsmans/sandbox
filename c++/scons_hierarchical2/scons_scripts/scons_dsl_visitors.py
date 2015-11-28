import os
Import('env')

env.Debug('### Adding Scons DSL Visitors')

# ############################################################
# visit sub directories that contains SConscripts that visit other sub directories
def visitSubDirectories(env, subdirs):
	for subdir in subdirs:
		env.SConscript(os.path.join(subdir, 'SConscript'), exports = ['env'])
env.AddMethod(visitSubDirectories, "VisitSubDirectories");

# ############################################################
# visit sub directories that contain a SConscript that build a library or executable
def visitBuildDirectories(env, subdirs):
	for subdir in subdirs:
	  # structure must be in sync with choice in createClone
	  # that uses variant_dir/../.. to find target directory !!
		env.SConscript(os.path.join(subdir, 'SConscript'), exports = ['env'],
			variant_dir=env.Settings().GetVariantDir()+'/'+subdir, duplicate=0)
env.AddMethod(visitBuildDirectories, "VisitBuildDirectories");

# ############################################################
# find directories recursively that contain SConscripts that build library or executable
def findVisitBuildDirectories(env, subdirs):
	import os, posixpath
	import datetime as dt
	env.Debug('FindVisitBuildDirectories ENTER')
	n1=dt.datetime.now()
	for subdir in subdirs:
		# root: Current path which is "walked through"
		# subdirs: Files in root of type directory
		# files: Files in root (not in subdirs) of type other than directory
		rootdir = os.path.join(Dir('#').abspath, subdir)
		env.Debug('. rootdir: %s' % rootdir)
		for root, subdirs, files in os.walk(rootdir):
			env.Debug('FindVisitBuildDirectories.root: %s' % root)
			sconscriptFile = os.path.join(root, 'SConscript')
			if os.path.exists(sconscriptFile):
				if os.path.isfile(sconscriptFile):
					nameDirSconscript = os.path.basename(root)
					env.Debug('FindVisitBuildDirectories.sconscriptFile: %s' % sconscriptFile)
					env.Debug('FindVisitBuildDirectories.nameDirSconscript: %s' % nameDirSconscript)
	  			# structure must be in sync with choice in createClone
	  			# that uses variant_dir/../.. to find target directory !!
					env.SConscript(sconscriptFile, exports = ['env'],
						variant_dir=root+'/../'+env.Settings().GetVariantDir()+'/'+nameDirSconscript, duplicate=0)
				else:
					raise Execption('Found directory called Sconscript: %s' % sconscriptFile)
	n2=dt.datetime.now()
	env.Debug('FindVisitBuildDirectories LEAVE, completed in %d ms' % ((n2-n1).microseconds / 1000))
env.AddMethod(findVisitBuildDirectories, "FindVisitBuildDirectories");

