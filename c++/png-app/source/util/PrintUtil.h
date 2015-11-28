/*
 * PrintUtil.h
 *
 *  Created on: Sep 6, 2014
 *      Author: martien
 */

#ifndef PRINTUTIL_H_
#define PRINTUTIL_H_

class PrintUtil {
public:
	PrintUtil();
	~PrintUtil();

	static void printHex(const void *ptr, const int nrOfBytes) {
		PrintUtil::printHex(ptr, nrOfBytes, 16);
	}
	static void printHex(const void *ptr, const int nrOfBytes,  const int nrOfBytesPerRow);
};

#endif /* PRINTUTIL_H_ */
