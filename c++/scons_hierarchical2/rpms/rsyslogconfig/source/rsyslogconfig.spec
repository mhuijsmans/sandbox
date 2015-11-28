# copied from:
# http://stackoverflow.com/questions/880227/what-is-the-minimum-i-have-to-do-to-create-an-rpm-file

# Don't try fancy stuff like debuginfo, which is useless on binary-only
# packages. Don't strip binary too
# Be sure buildpolicy set to do nothing
%define        __spec_install_post %{nil}
%define          debug_package %{nil}
%define        __os_install_post %{_dbpath}/brp-compress

Summary: A simple binary rpm package
Name: [[name]]
Version: [[version]]
Release: [[release]]
License: Todo
Group: Development/Tools
SOURCE0 : temp.tar.gz
# interesting example
# Source1: http://svn.debian.org/viewsvn/python-modules/packages/python-trml2pdf/trunk/debian/%{srcname}.1
# interesting example
# Source1: http://svn.debian.org/viewsvn/python-modules/packages/python-trml2pdf/trunk/debian/%{srcname}.1
URL: http://toybinprog.company.com/
BuildRoot: %{_topdir}/tmp/%{name}-%{version}-%{release}-buildroot
# BuildRequires: kdebase3-devel
# Prereq: /sbin/ldconfig
# ###
# manging groups & users
# ref: http://superuser.com/questions/168461/managing-service-accounts-in-an-rpm-spec
# next link is a discussion on adding & deleting user (any why user/group shall not be deleted)
# ref: https://fedoraproject.org/wiki/Packaging:UsersAndGroups
Requires(pre): /usr/sbin/useradd, /usr/bin/getent
Requires(postun): /usr/sbin/userdel

%description
This is an toy package

%{summary}

%prep
echo Building: %{name}-%{version}-%{release}
echo Buildroot: %{buildroot}

%setup -q

%build
# Empty section.

%install
rm -rf %{buildroot}
mkdir -p  %{buildroot}
# in builddir
cp -a * %{buildroot}

# directories to clean before creating the RPM
%clean
rm -rf %{buildroot}

# %files — A list of files to be included in the package
# The list consists of one file per line. If a directory is specified, 
# by default all files and subdirectories will be packaged.
%files
# defattr sets default %attr for RPM
#format %attr(<file mode>, <user>, <group>, <dir mode>)
%defattr(-,root,root,-)
# %config used to mark config files so that edits to config files 
#    won't get lost during a subsequent upgrade
#    ref: http://www-uxsup.csx.cam.ac.uk/~jw35/docs/rpm_config.html
%config(noreplace) %{_sysconfdir}/%{name}/%{name}.conf
# _bindir is a macro. However I do not understand logic to include it here.
# discussed here, e.g. howto print: 
#    http://stackoverflow.com/questions/8076471/how-to-know-the-value-of-built-in-macro-in-rpm
# overview: http://rpm.org/api/4.4.2.2/config_macros.html
%{_bindir}/*

%changelog
* Tue Mar 22 2005 - jane (at) jungle.world
- Calling tarzan

* Tue Mar 22 2005 - tarzan (at) jungle.world
- Looking for Jane
- Buying flowers
