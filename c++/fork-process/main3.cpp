#include <stdio.h>
#include <stdlib.h>

// source: http://www.yolinux.com/TUTORIALS/ForkExecProcesses.html
main()
{
   FILE *fpipe;
   char *command = (char *)"ls";
   char line[256];
   
   // calling a system process
   if ( !(fpipe = (FILE*)popen(command,"r")) )
   {  // If fpipe is NULL
      perror("Problems with pipe");
      exit(1);
   }

   while ( fgets( line, sizeof line, fpipe))
   {
     printf("%s", line);
   }
   pclose(fpipe);
   
   // using a blocking system call
   system("ls -l");
   printf("Command done!");   
   
   // Calling an own process
   command = (char *)"./main1";
   if ( !(fpipe = (FILE*)popen(command,"r")) )
   {  // If fpipe is NULL
      perror("Problems with pipe");
      exit(1);
   }

   while ( fgets( line, sizeof line, fpipe))
   {
     printf("%s", line);
   }
   pclose(fpipe);
  
}
     