/*
 * PrintUtil.cpp
 *
 *  Created on: Sep 6, 2014
 *      Author: martien
 */

#include <iomanip>
#include <iostream>

#include "PrintUtil.h"

PrintUtil::PrintUtil() {
	// TODO Auto-generated constructor stub

}

PrintUtil::~PrintUtil() {
	// TODO Auto-generated destructor stub
}

void PrintUtil::printHex(const void *ptr, const int nrOfBytes, const int nrOfBytesPerRow) {
	int cntr = 0;
	for (int i = 0; i < nrOfBytes; ++i) {
		if (cntr == nrOfBytesPerRow) {
			std::cout << std::endl;
			cntr = 0;
		}
		int value = ((unsigned char *) ptr)[i] & 0xff;
		if (cntr > 0) {
			std::cout << " ";
		}
		std::cout << std::hex << std::setfill('0') << std::setw(2) << value;
		cntr++;
	}
	std::cout << std::endl;
	std::cout << std::dec;
}

