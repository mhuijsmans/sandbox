#ifndef EnvironmentVariablesFile_h_
#define EnvironmentVariablesFile_h_

#include <string>

class EnvironmentVariablesFile
{
 public:
    EnvironmentVariablesFile();
   ~EnvironmentVariablesFile();
    char **ReadFile(std::string& envFile);
	int Size() { return m_NumberOfEnvironmentVariables; }
 private:
    int m_NumberOfEnvironmentVariables;
    char **m_envp;
};

#endif 