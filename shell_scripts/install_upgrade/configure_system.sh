#!/bin/bash

if [[ $(id -u) -ne 0 ]] ; then echo "Error: must be run as root" ; exit 1 ; fi

# installation may require actions on home directory
USER=$SUDO_USER
USER_HOME=/home/$USER
# sanity check
if [ ! -d "$USER_HOME" ]; then
  echo "Implementation error: home dir doesn't exist $USER_HOME"
  exit 1
fi

# When packages is not installed yum will print error message
# example: $ yum list installed kdbg
# Loaded plugins: langpacks, refresh-packagekit
# Error: No matching Packages to list
function isPackagedInstalled {
  echo "> checking installation of $1"
  COMMAND="yum list installed $1 2>&1 | grep -i 'error'"
  OUTPUT=$(eval $COMMAND)
  if [ -n "$OUTPUT" ]; then
    return 1
  else
    return 0
  fi
}

function installPackage {
    echo "> not installed, starting installation"
    sudo yum -y install $1
}

function installPackageIfNotInstalled {
  if isPackagedInstalled $1; then
    echo "> already installed, skipping"
  else
    installPackage $1
  fi
}

function installPackages {
  for var in "$@"
  do
    echo "Package: $var"
    installPackageIfNotInstalled "$var"
  done
}

######################################
# MAIN

# Packages to install
PACKAGES=(
    ### Tool-chain
    gcc-c++
    curl
    libcurl-devel
    rpm-build
    rpmlint
    scons
    valgrind
    kdbg
    ### scm
    rapidsvn
    # rabbitvcs requires hone-shell-extensions; but rabbitvcs is not my favorite
    # gnome-shell-extension*
    # rabbitvcs*
    ### reporting
    texlive
    texlive-preprint
    python-matplotlib
    ### media
    libpng
    libpng-devel
    gimp
    ImageMagick
    ### XSD-CXX
    xerces-c
    xerces-c-devel
    ### to prevent certain packages from being updated
    yum-plugin-versionlock
    ### troubleshhoting
    wireshark-gnome
    ### UI
    gnome-tweak-tool
    nautilus-open-terminal
)

installPackages ${PACKAGES[*]}

echo "You may need to restart"

exit 0