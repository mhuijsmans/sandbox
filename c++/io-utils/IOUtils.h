/*
 * Filename: IOUtils.h
 */

#ifndef IOUTILS_H_
#define IOUTILS_H_

#include <string>

class IOUtils {
public:
	static bool DeleteFile(const std::string &fileName);
	static void CopyFile(const std::string &srcFileName, const std::string &destFile);
	static std::string ReadFileToString(const std::string &fileName);
private:
	IOUtils();
	virtual ~IOUtils();
};

#endif /* IOUTILS_H_ */
