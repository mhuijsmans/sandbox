#!/bin/sh

# Sublime Text 3 install with Package Control
#
#     www.simonewebdesign.it/install-sublime-text-3-on-linux/
#
# Run this script with:
#
#     curl -L git.io/sublimetext | sh
#
# mahu: above command looks cools.
#
# Found script at:
# http://www.simonewebdesign.it/install-sublime-text-3-on-linux/
# ref: https://gist.github.com/simonewebdesign/8507139

PWD=`pwd`
echo "pwd : $PWD"
echo "home: $HOME"

[ "$PWD" = "$HOME" ] || echo "Script must execute in home directory"; exit 1

# Detect the architecture
UNAME=$(uname -m)
if [ "$UNAME" = 'x86_64' ]; then
  ARCHITECTURE="x64"
else
  ARCHITECTURE="x32"
fi

SUBLIME_TAR_GZ="sublime_text_3_build_3065_$ARCHITECTURE.tar.bz2"

# Download the tarball, unpack and install
URL="http://c758482.r82.cf2.rackcdn.com/$SUBLIME_TAR_GZ"
INSTALLATION_DIR="/opt/sublime_text"

curl -o $HOME/$SUBLIME_TAR_GZ $URL
if tar -xf $HOME/$SUBLIME_TAR_GZ --directory=$HOME; then
  sudo mv $HOME/sublime_text_3 $INSTALLATION_DIR
  sudo ln -s $INSTALLATION_DIR/sublime_text /bin/subl
fi
rm $HOME/$SUBLIME_TAR_GZ

# Package Control - The Sublime Text Package Manager: https://sublime.wbond.net
curl -o $HOME/Package\ Control.sublime-package https://sublime.wbond.net/Package%20Control.sublime-package
sudo mv $HOME/Package\ Control.sublime-package "$INSTALLATION_DIR/Packages/"

# Add to applications list (thanks 4ndrej)
sudo ln -s $INSTALLATION_DIR/sublime_text.desktop /usr/share/applications/sublime_text.desktop

echo ""
echo "Sublime Text 3 installed successfully!"
echo "Run with: subl"
