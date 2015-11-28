#!/bin/bash 

# variables and functions are defined first, 
# next the main 

if [[ $(id -u) -ne 0 ]] ; then echo "Error: must be run as root" ; exit 1 ; fi

REPO_CONFIG_FILE_NAME=sgs-yumrepo-build.repo
YUM_REPO_PATH=/etc/yum.repos.d
REPO_CONFIG_FILE=$YUM_REPO_PATH/$REPO_CONFIG_FILE_NAME 
SERVER_URL=http://192.168.14.96

# installation may require actions on home directory
USER=$SUDO_USER
USER_HOME=/home/$USER
# sanity check
if [ ! -d "$USER_HOME" ]; then
  echo "Implementation error: home dir doesn't exist $USER_HOME"
  exit 1
fi

function download_and_install-repo-config {
  if [ ! -f $REPO_CONFIG_FILE ]; then
    echo "File $REPO_CONFIG_FILE not found!"
    wget $SERVER_URL/$REPO_CONFIG_FILE_NAME
    if [ -f $REPO_CONFIG_FILE_NAME ]; then
      sudo mv $REPO_CONFIG_FILE_NAME $YUM_REPO_PATH
    else 
      echo "Download of $REPO_CONFIG_FILE_NAME failed"
      exit 1
    fi
  fi
}  

function calc_random_value {
  # return value does not exists, so set global variable
  # single quotes in next line doesn't work; double does
  random_value=`awk "BEGIN{srand();print int(rand()*($2 - $1)) + $1 }"`
  # echo "random_value: $random_value"
}

function check_if_installed {
  echo "Checking for installation of: $1"  
  if [ "`yum list installed | grep $1`" ] ; then
    echo "Is installed: $1"
  else
    echo "Not installed: $1"
  fi
}

function remove_package_if_installed {
  if [ "`yum list installed | grep $1`" ] ; then
    sudo yum -y remove $1
    echo "Removed $1"
  else 
    echo "Not installed $1"
  fi
}

function remove_all_packages_if_installed {
  for var in "$@"
  do
    remove_package_if_installed "$var"
  done
}

function install_package {
  sudo yum -y install $1
}

function install_all_packages {
  for var in "$@"
  do
    install_package "$var"
  done
}

function upgrade_package {
  sudo yum -y upgrade $1
}

function upgrade_all_packages {
  for var in "$@"
  do
    upgrade_package "$var"
  done
}

function remove_install_package {
  remove_package_if_installed "$1"
  install_package "$1"
}

function remove_install_all_packages {
  for var in "$@"
  do
    remove_install_package "$var"
  done
}

function upgrade_install_package {
  if [ "`yum list installed | grep $1`" ] ; then
    upgrade_package $1
  else 
    install_package $1
  fi
}

function upgrade_install_all_packages {
  for var in "$@"
  do
    upgrade_install_package "$var"
  done
}

######################################
# The script starts here

download_and_install-repo-config

# generate a random number between min(0, inclusive)-max(10, exclusive) 
calc_random_value 0 10
# 20% probability for clean install
if [[ $random_value -ge 0 && $random_value -le 1 ]] ; then
  # clean install
  remove_install_all_packages xyz1_rpm xyz2_rpm
else
  # upgrade
  upgrade_install_all_packages xyz1_rpm xyz2_rpm
fi

# ##########################################
# Installation that first updated yum config
if isinstalled google-chrome-stable; then

# create chrome repo (overwritting if it exists)
cat << EOF > /etc/yum.repos.d/google-chrome.repo
[google-chrome]
name=google-chrome - \$basearch
baseurl=http://dl.google.com/linux/chrome/rpm/stable/\$basearch
enabled=1
gpgcheck=1
gpgkey=https://dl-ssl.google.com/linux/linux_signing_key.pub
EOF

yum -y install google-chrome-stable

fi

exit 0


