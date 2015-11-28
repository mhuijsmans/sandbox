/*
 * FileGrep.cpp
 */

#include <iostream>
#include <stdio.h>
#include <stdlib.h>

#include <FileGrep.h>

FileGrep::FileGrep() {
}

FileGrep::~FileGrep() {
}

bool FileGrep::GrepTextInVarLogMessages(const std::string &textToGrep) {
	return GrepText("/var/log/messages", textToGrep);
}

bool FileGrep::GrepText(const std::string &fileName, const std::string &textToGrep) {
	FILE *fpipe;
	std::string command("cat ");
	command.append(fileName).append(" | grep ").append(textToGrep);
	char line[256] = { 0 }; // set complete array to 0
	std::cout << "LogfileGrep::GrepLogMessages, executing command: " << command
			<< std::endl;
	// calling a system process
	if (!(fpipe = (FILE*) popen(command.c_str(), "r"))) {  // If fpipe is NULL
		std::cout << "LogfileGrep::GrepLogMessages: error, problems with pipe"
				<< std::endl;
		return false;
	}
	std::cout << "LogfileGrep::GrepLogMessages, reading data" << std::endl;
	if (fgets(line, sizeof line, fpipe)) {
		std::cout << "LogfileGrep::GrepLogMessages: line" << line << std::endl;
	} else {
		std::cout << "LogfileGrep::GrepLogMessages, reading data failed"
				<< std::endl;
	}
	pclose(fpipe);
	return line[0] != 0;
}

