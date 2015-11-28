/*
 * FileGrep.h
 */

#ifndef FILEGREP_H_
#define FILEGREP_H_

#include <string>

class FileGrep {
public:
	static bool GrepTextInVarLogMessages(const std::string &textToGrep);
	static bool GrepText(const std::string &fileName,
			const std::string &textToGrep);
private:
	FileGrep();
	virtual ~FileGrep();
};

#endif /* FILEGREP_H_ */
