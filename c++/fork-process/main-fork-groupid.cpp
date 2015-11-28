#include <iostream>

#include "stdlib.h" 
#include "stdio.h"
#include "errno.h"
#include "unistd.h"
#include "string.h"
#include "sys/stat.h"
#include "sys/types.h"
#include "sys/ipc.h"
#include "sys/shm.h"
#include "signal.h"
#define PERM S_IRUSR|S_IWUSR

void sleepForever() {
	for (;;)
		pause();
}

void sig_usr3(int signo) {
	if (signo == SIGUSR1)
		printf("received signal in process 3\npid is %d\n\n", getpid());
	exit(0);
}

void sig_usr4(int signo) {
	if (signo == SIGUSR1)
		printf("received signal in process 4\npid is %d\n\n", getpid());
	exit(0);
}

void sig_usr5(int signo) {
	if (signo == SIGUSR1)
		printf("received signal in process 5\npid is %d\n\n", getpid());
	exit(0);
}

// doc on shared-memory: http://www.makelinux.net/alp/035
// After a run I saw the following processes left:
//    4335 pts/0    00:00:00 main-fork-6
//    4338 pts/0    00:00:00 main-fork-6 <defunct>
// 4338 is parent of 4335, which was still alive.

// example uses groupId to ensure that when group is terminated
// that also child terminates.


int main() {
	size_t msize;
	key_t shmid;
	pid_t *pid;

	std::cout << "Share Memory / fork example" << std::endl;

	// CReate shared memory where the forked processes will store their PID
	msize = 6 * sizeof(pid_t);
	if ((shmid = shmget(IPC_PRIVATE, msize, PERM)) == -1) {
		std::cerr << "Share Memory Error:" << strerror(errno) << std::endl;
		exit(1);
	}
	pid = static_cast<pid_t *>(shmat(shmid, 0, 0));
	memset(pid, 0, msize);

	//
	pid[0] = getpid();

	//process 0
	if (fork() == 0) {
		std::cout << "Process-1: " << getpid() << std::endl;
		//process 1
		pid = static_cast<pid_t *>(shmat(shmid, 0, 0));
		pid[1] = getpid();
		if (fork() == 0) {
			std::cout << "Process-5: " << getpid() << std::endl;
			//process 5
			pid = static_cast<pid_t *>(shmat(shmid, 0, 0));
			std::cout << "Process-5 waiting for process 3" << std::endl;
			while (pid[3] == 0)
				sleep(1);
			std::cout << "Process-5 found process 3, joining group" << std::endl;
			if ((setpgid(getpid(), pid[3])) == -1)
				printf("pid5 setpgid error.\n");
			pid[5] = getpid();
			signal(SIGUSR1, sig_usr5);
			sleepForever();
		}
		sleepForever();
		exit(0);
	}

	if (fork() == 0) {
		std::cout << "Process-2: " << getpid() << std::endl;
		//process 2
		pid = static_cast<pid_t *>(shmat(shmid, 0, 0));
		pid[2] = getpid();

		if (fork() == 0) {
			std::cout << "Process-3: " << getpid() << std::endl;
			//process 3
			pid = static_cast<pid_t *>(shmat(shmid, 0, 0));
			if ((setpgid(getpid(), getpid())) == -1) {
				printf("pid3 setpgid error.\n");
			}
			pid[3] = getpid();

			if (fork() == 0) {
				std::cout << "Process-4: " << getpid() << std::endl;
				//process 4
				pid = static_cast<pid_t *>(shmat(shmid, 0, 0));
				pid[4] = getpid();
				if ((setpgid(pid[4], pid[3])) == -1) {
					printf("pid4 setpgid error.\n");
				}
				signal(SIGUSR1, sig_usr4);
				sleepForever();
			} else {
				signal(SIGUSR1, sig_usr3);
				sleepForever();
			}
			for (;;) {
				sleep(1);
			}
		}
		// process 2 terminates by itself. But it's children continue to live.
	}

	if (getpid() == pid[0]) {
		sleep(5);
		std::cout << "Waiting until all processes forked" << std::endl;
		int i, flag;
		while (!(pid[0] && pid[1] && pid[2] && pid[3] && pid[4] && pid[5])) {
			sleep(1);
		}
		std::cout << "All processes have forked" << std::endl;

		for (i = 0; i < 6; i++) {
			std::cout << " Killing: " << "process " << i << "-" << pid[i]
					<< std::endl;
		}
		// Kill process group
		kill(-pid[3], SIGUSR1);
		// kill process 1
		kill(pid[1], SIGUSR1);
	}
}
