The project explores building a RPM in maven using the content of the current project.
It deployed a webserver (http://localhost:8888) as a service (init.d).

Notes
-----
maven-rpm-plugin executes in default in phase package the goal rpm.

Open: can a maven-plugin add dependencies to another a rpm. 

Lessons
-------
pre/post (un)install scripts are executed as root:root with cwd is /.
   So a file that is created will default have user:group of root:root
files created by the pre/post (un)install scripts also need to be deleted by
   these scripts. Otherwise file will remain when rpm is removed.
If, at "rpm remove ..", no files are added to /opt/maven-rpmtest, than
   /opt/maven-rpmtest is removed. Otherwise directory will continue to exist.
In case of upgrade
- new package is installed (pre/post install)
- old package is uninstalled (pre/post uninstall)

Understanding the RPM
---------------------
This section aims to describe what RPM "is" & properties of the structure. 
The RPM builder has to do the create the spec to create a target RPM.

A RPM can installs software.
A RPM created user / groups / ..
A RPM add repo entry to yum
A RPM can configure logrotate

From jenkins:
Possible structure RPM, where NAME is name of the app (lowercase), e.g. Jenkins
etc/init.d/NAME
etc/logrotate.d/NAME
etc/sysconfig/NAME
etc/yum.repos.d/NAME.repo
usr/lib/NAME/NAME.war
usr/sbin/rcNAME
var/cache/NAME
var/lib/NAME
var/log/NAME

Note rcNAME could contain:
../../etc/init.d/NAME

From tomcat:
/usr/share/doc/NAME/     different files
/usr/share/tomcat        bin and symbolic links to conf, lib, logs, temp, webapss and work
/usr/share/tomcat/bin    bootstrap.jar and more   
/usr/lib/systemd/        contains systemd configuration
   
RPM: systemd versus init.d
--------------------------
System.d is not supported on Centos. Nice discussion:
http://www.reddit.com/r/linux/comments/132gle/eli5_the_systemd_vs_initupstart_controversy/ 
   
RPM
---
%config and how affect already deployed files.
http://www-uxsup.csx.cam.ac.uk/~jw35/docs/rpm_config.html

The rpm command will pass one argument to your scripts (as $1),  
which holds a count of the number of versions of the package that are installed.
Install the first time:         1
Upgrade:                        2 or higher (depending on the number of versions installed)
Remove last version of package: 0

Open: as what used for an install script run ?
Open: constraints 
 
See table 10.3 at:
http://docs.fedoraproject.org/en-US/Fedora_Draft_Documentation/0.1/html/RPM_Guide/ch09s04s05.html

Testing a RPM
-------------
tools: rpmlint

RPM Best practices
------------------
Is there are multiple ears, package each ear in a own rpm. 
If you have multiple ear rpm's, you can have an empty rpm that Requires all of them to make a bundle, 
or you could make them a yum group.

There is a convention to start a script with a RPM section marker for the given script:
- %pre,    for the pre-install script. 
- %post,   post-install
- %preun,  pre-uninstall
- %postun, post-uninstall

References
----------
This was my first RPM project. It has doc + git-repo
http://rombertw.wordpress.com/2010/05/20/maven-recipe-delivering-applications-as-rpms/ 