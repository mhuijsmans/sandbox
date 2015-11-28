# .bashrc

# Source global definitions
if [ -f /etc/bashrc ]; then
	. /etc/bashrc
fi

# Uncomment the following line if you don't like systemctl's auto-paging feature:
# export SYSTEMD_PAGER=

# User specific aliases and functions

sconsv() {
  local P=$(pwd)

  scons "$@" verbose=1 -j 16 2>&1 | tee $P/SCONS.log
}

sconsf() {
  local P=$(pwd)

  scons "$@" -j 16
}

mem_check() {
  local P=$(pwd)

  valgrind --leak-check=full --show-leak-kinds=all "$*" 2>&1 | tee $P/mem_check.result
}

