/*
 * Utils.h
 *
 *  Created on: Aug 28, 2014
 *      Author: martien
 */

#ifndef UTILS_H_
#define UTILS_H_

class Utils {
public:
	Utils();
	virtual ~Utils();

	static void printHex(const void *p, const int nrOfBytes);
};

#endif /* UTILS_H_ */
