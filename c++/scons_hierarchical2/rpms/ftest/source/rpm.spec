# Don't try fancy stuff like debuginfo, which is useless on binary-only
# packages. Don't strip binary too
# Be sure buildpolicy set to do nothing
%define        __spec_install_post %{nil}
%define          debug_package %{nil}
%define        __os_install_post %{_dbpath}/brp-compress

Summary: RPM package for app1
Name: [[name]]
Version: [[version]]
Release: [[release]]
License: Todo
Group: Development/Tools
SOURCE0 : temp.tar.gz
BuildRoot: %{_topdir}/tmp/%{name}-%{version}-%{release}-buildroot

%description
This is an package for app1

%{summary}

%prep
echo Building: %{name}-%{version}-%{release}

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
%defattr(-,ftest,ftest,-)
# _bindir is a macro. However I do not understand logic to include it here.
# discussed here, e.g. howto print: 
#    http://stackoverflow.com/questions/8076471/how-to-know-the-value-of-built-in-macro-in-rpm
# overview: http://rpm.org/api/4.4.2.2/config_macros.html
%{_bindir}/*

