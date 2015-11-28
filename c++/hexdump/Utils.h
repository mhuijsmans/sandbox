/*
 * Utils.h
 *
 *  Created on: Aug 28, 2014
 *      Author: martien
 */

#ifndef UTILS_H_
#define UTILS_H_

#include <string>

class Utils {
public:
	Utils();
	virtual ~Utils();

	static void printHex(const void *p, const int nrOfBytes);
	static std::string ConvertToHexDump(const void *p, const int nrOfBytes);
};

#endif /* UTILS_H_ */
