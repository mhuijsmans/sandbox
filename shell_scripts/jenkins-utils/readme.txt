This project contains shell scripts for Jenkins workflows.
These scripts were developed to be part of a maven workflow, where certain steps
in the workflow involve execution of scripts (using maven-exec-plugin).

The sleep 1 minutes (in createrepo) is needed, because createrepo starts a background process.
That takes some time to complete.

yum -y install packagename
The "-y" implies "yes".
Without the -y, yum will prompt y/n for an install.

-------------------

If execution of the script needs to be done with sudo, you needs to take some
extra steps to make things work.
Assume goal is allow Jenkins to execute script /opt/publish_rpm/publish with sudo.
Assumption: /opt/publish_rpm/publish exists.
Steps:
Open sudoers
$ sudo visudo
and add
jenkins    ALL = NOPASSWD: /opt/publish_rpm/publish
( Ref:
http://stackoverflow.com/questions/11880070/how-to-run-the-script-as-root-in-jenkins )
comment out: #Default requiretty
(when present, a sudo command can only be executed using a tty).
