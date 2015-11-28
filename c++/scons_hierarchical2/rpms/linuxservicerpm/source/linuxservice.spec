# directories containing data
%define workdir /usr/bin/%{name}
%define libdir /usr/lib/%{name}
%define systemddir /usr/lib/systemd/system

# One line summary of package. Do not end with period
Summary: RPM package for linuxservice
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
# next line prevents running scripts during RPM installation that check if all dynamic lib dependencies can be resolved.
# scripts are listed here: http://www.rpm.org/max-rpm/s1-rpm-depend-auto-depend.html
Autoreq: no
# There is also a autoreqprov, which is used at package creation time.
# http://www.rpm.org/max-rpm/s1-rpm-inside-tags.html#S3-RPM-INSIDE-AUTOREQPROV-TAG
# The autoreqprov tag is used to control the automatic dependency processing performed when the package is being built.
# BUT it doesn't help to find the dynamic libraries packaged in the RPM.
AutoReqProv: no

# A longer, multi-line description of the program.
%description
This is an package for linuxservice

# TODO: what is next line
%{summary}

# RPMBUILD-stage: read sources & patches. Writes output to
%prep
echo MAHU-Building: %{name}-%{version}-%{release}
echo MAHU-buildroot: %{buildroot}
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
echo MAHU-CP completed

# RPMBUILD-stage; run test(suite) to check that software works properly.
# Mostly not used. Example: make test.
%check
echo MAHU-CHECK completed

# RPMBUILD-substage: directories to clean before creating the RPM
%clean
rm -rf %{buildroot}
echo MAHU-RM-RF completed

# RPMINSTALL-stage: executed before RPM installation on target system
# /bin/false is a binary that immediately exits, returning false, when its called.
# So when someone who has false as shell logs in, they're immediately logged out when false exits.

# pre script is run before package is installed
%pre
# create group: martien
/usr/sbin/groupadd -r martien &>/dev/null || :
# create user: mahutest and add to group
/usr/sbin/useradd -g "martien" -s "/bin/false" -r -c "Test application" -d "%{workdir}" "mahutest" &>/dev/null || :

# post script is run after package is installed
# if maiden install (i.e. no previous installed version), start the linux service.
%post
if [ $1 = 1 ]; then
    systemctl start linuxservice > /dev/null 2>&1
fi

# preun script is run BEFORE package is uninstalled
%preun
# if uninstall, then stop service
# for description of $1 see: http://fedoraproject.org/wiki/Packaging:ScriptletSnippets
if [ $1 = 0 ]; then
    systemctl stop linuxservice > /dev/null 2>&1
fi

# preun script is run AFTER package is uninstalled
# if upgrade, restart (i.e. stop old and start new version)
%postun
if [ "$1" -gt 1 ]; then
    systemctl restart linuxservice > /dev/null 2>&1
fi

## #######################################################################
# %files — contains a number of different directives.
# ref: http://www.rpm.org/max-rpm/s1-rpm-inside-files-list-directives.html
%files
# The %files section must list all files in $RPM_BUILD_ROOT directory.
#
# %defattr sets the default %attr for RPM
# format %defattr(<file mode>, <user>, <group>, <dir mode>)
# filemode = owner-group-world
%defattr(-,root,root,-)

# 7=rwx, 5=rx, 0=-
%attr(755,mahutest,martien) %dir %{workdir}
# Executable can only be executed by group or owner
%attr(750,mahutest,martien) %{workdir}/linuxservice

# 750 may be good value for e.g. logfile

# %config used to mark config files so that edits to config files
#    won't get lost during a subsequent upgrade
#    ref: http://www-uxsup.csx.cam.ac.uk/~jw35/docs/rpm_config.html
%config(noreplace) %{_sysconfdir}/%{name}/%{name}.conf
%config(noreplace) %{_sysconfdir}/sysconfig/%{name}

#
# Hereafter list all the files and directories to be OWNED by the package.

# if next dir is empty you get an error: File not found by glob .....
%{libdir}/*
%{systemddir}/*
