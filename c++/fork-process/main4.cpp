#include <iostream>
#include <sys/types.h>
#include <unistd.h>

#include "GetErrMsg.h"
#include "EnvironmentVariablesFile.h"

// source: http://www.yolinux.com/TUTORIALS/ForkExecProcesses.html

using namespace std;

main()
{
   string envFile("environment_variables.conf");
   EnvironmentVariablesFile readEnvFile;
   char **Env_envp = readEnvFile.ReadFile(envFile);
   std::cout << "Number of environment variables=" << readEnvFile.Size() << std::endl;

   // Command to execute
   char *Env_argv[] = { (char *)"/bin/ls", (char *)"-l", (char *)"-a", (char *) 0 };

   pid_t pID = fork();
   if (pID == 0)                // child
   { 
      // This version of exec accepts environment variables.
      // Function call does NOT return on success.
      errno = 0;
      int execReturn = execve (Env_argv[0], Env_argv, Env_envp);
      if(execReturn == -1)
      {
         std::cout << "Failure! execve error code=" << errno << std::endl;
         std::cout << GetErrMsg(errno) << std::endl;
      } else {
	    std::cout << "Succesfully invoked execve" << std::endl;
	  }

      _exit(0); // If exec fails then exit forked process.
   }
   else if (pID < 0)             // failed to fork
   {
      std::cerr << "Failed to fork" << std::endl;
   }
   else                             // parent
   {
      std::cout << "Parent Process" << std::endl;
   }
 
   _exit(0);
}