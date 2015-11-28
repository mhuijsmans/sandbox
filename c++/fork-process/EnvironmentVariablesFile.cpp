#include <iostream>
#include <fstream>
#include <string>
#include <vector>

// Required by for routine
#include <sys/types.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>

#include "EnvironmentVariablesFile.h"

using namespace std;

EnvironmentVariablesFile::EnvironmentVariablesFile() :
m_NumberOfEnvironmentVariables(0), m_envp(0)
{ }

//   Free memory:

EnvironmentVariablesFile::~EnvironmentVariablesFile()
{
   int ii;

   // Free array's of characters
   for(ii=0; ii < m_NumberOfEnvironmentVariables; ii++)
   {
      delete [] m_envp[ii];
   }

   // Free array of pointers.
   delete [] m_envp;
}

char **
EnvironmentVariablesFile::ReadFile(string& envFile)
{
   int ii;
   string tmpStr;
   vector<string> vEnvironmentVariables;

   if( envFile.empty() ) return 0;

   ifstream inputFile( envFile.c_str(), ios::in);
   if( !inputFile )
   {
       cerr << "Could not open config file: " << envFile << endl;
       return 0;
   }

   while( !inputFile.eof() )
   {
       getline(inputFile, tmpStr);
       if( !tmpStr.empty() ) vEnvironmentVariables.push_back(tmpStr);
   }

   inputFile.close();

   m_NumberOfEnvironmentVariables = vEnvironmentVariables.size();

   // ---------------------------------------
   // Generate envp environment variable list
   // ---------------------------------------

   // Allocate pointers to strings.
   // +1 for array terminating NULL string

   m_envp = new char * [m_NumberOfEnvironmentVariables + 1];

   // Allocate arrays of character strings.
   
   for(ii=0; ii < m_NumberOfEnvironmentVariables; ii++)
   {
      // Character string terminated with a NULL character.
      m_envp[ii] = new char [vEnvironmentVariables[ii].size()+1];
      strcpy( m_envp[ii], vEnvironmentVariables[ii].c_str());
   }

   // must terminate array with null string
   m_envp[ii] = (char*) 0;

   return m_envp;
}

                