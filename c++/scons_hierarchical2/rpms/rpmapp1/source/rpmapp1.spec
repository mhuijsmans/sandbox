%define workdir	%{_bindir}/app1

# One line summary of package. Do not end with period
Summary: RPM package for app1
# Name of package
Name: [[name]]
# Version of package
Version: [[version]]
Release: [[release]]
License: Todo
# Group needs to be a pre-existing group
Group: Development/Tools
# The complete source resides in temp.tar.gz
Source: temp.tar.gz
# Buildroot is where files are installed (e.g. during install-stage)
BuildRoot: %{_topdir}/tmp/%{name}-%{version}-%{release}-buildroot
#
Requires: /usr/sbin/groupadd /usr/sbin/useradd

# A longer, multi-line description of the program.
%description
This is an package for app1

# TODO: what is next line
%{summary}

# RPMBUILD-stage: read sources & patches. Writes output to  
%prep
echo Building: %{name}-%{version}-%{release}
# next line tell to supress unnecessary output 
%setup -q

# RPMBUILD-stage, where sources are compiled
%build
# Empty section.

# RPMBUILD-stage; copy data (e.g. from buildir) to  %{buildroot} 
%install
rm -rf %{buildroot}
mkdir -p  %{buildroot}
# in builddir
cp -a * %{buildroot}

# RPMBUILD-stage; run test(suite) to check that software works properly.
# Mostly not used. Example: make test.
%check

# RPMBUILD-substage: directories to clean before creating the RPM
%clean
rm -rf %{buildroot}

# RPMINSTALL-stage: executed before RPM installation on target system
# /bin/false is a binary that immediately exits, returning false, when its called.
# So when someone who has false as shell logs in, they're immediately logged out when false exits. 
%pre
# create group: martien
/usr/sbin/groupadd -r martien &>/dev/null || :
# create user: mahutest and add to group 
/usr/sbin/useradd -g "martien" -s "/bin/false" -r -c "Test application" -d "%{workdir}" "mahutest" &>/dev/null || :

# %files — contains a number of different directives. 
# ref: http://www.rpm.org/max-rpm/s1-rpm-inside-files-list-directives.html
%files
#
# %defattr sets the default %attr for RPM
# format %defattr(<file mode>, <user>, <group>, <dir mode>)
# filemode = owner-group-world
%defattr(-,root,root,-)

# 7=rwx, 5=rx, 0=-
%attr(755,mahutest,martien) %dir %{workdir}
# Executable can only be executed by group or owner
%attr(750,mahutest,martien) %{workdir}/%{name}
# 750 may be good value for e.g. logfile
#
# %config used to mark config files so that edits to config files 
#    won't get lost during a subsequent upgrade
#    ref: http://www-uxsup.csx.cam.ac.uk/~jw35/docs/rpm_config.html
%config(noreplace) %{_sysconfdir}/%{name}/%{name}.conf
#
# Hereafter list all the files and directories to be OWNED by the package.
# The used NOTATION is through macro's are shown below.  
# Use macros for directory names where possible, which can be viewed at Packaging:RPMMacros 
# (e.g. use %{_bindir}/mycommand instead of /usr/bin/mycommand). 
# If the pattern begins with a "/" (or when expanded from the macro) then it is taken from 
# the %{buildroot} directory. Otherwise, the file is presumed to be in the current directory 
# (e.g. inside %{_builddir}, such as documentation files that you wish to include)
# ref: https://fedoraproject.org/wiki/How_to_create_an_RPM_package#The_basics_of_building_RPM_packages
# ref macros: https://fedoraproject.org/wiki/Packaging:RPMMacros 
# overview: http://rpm.org/api/4.4.2.2/config_macros.html
# %{_bindir} maps to /usr/bin
%{_bindir}/%{name}/*
# %{_sysconfdir} maps to /etc
%{_sysconfdir}/%{name}/*

