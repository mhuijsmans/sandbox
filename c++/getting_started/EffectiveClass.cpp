/*
 * EffectiveClass.cpp
 *
 *  Created on: Mar 14, 2014
 *      Author: martien
 */

// In C++ string.h defines strlen, strcpy in the global name space
#include <string.h>

#include "EffectiveClass.h"

EffectiveClass::EffectiveClass() {
	dynAllocatedMemory = new char[1];
	dynAllocatedMemory[0] = 0;
}

EffectiveClass::~EffectiveClass() {
	// Open issue: a destructor can be invoked multiple times
	//     that affect the code in assignment operator
	delete[] dynAllocatedMemory;
	dynAllocatedMemory = 0;
}

EffectiveClass::EffectiveClass(const EffectiveClass &rhs) {
	// intialize this class with the data from the provide class
	copy(rhs.dynAllocatedMemory);
}

// Item 11: include a assignment operator
EffectiveClass& EffectiveClass::operator=(const EffectiveClass &rhs) {
	if (this == &rhs) {
		return *this;
	}
	copy(rhs.dynAllocatedMemory);
	// item, 15: return a reference to *this
	return *this;
}

void EffectiveClass::copy(const char *src) {
	if (dynAllocatedMemory) {
		delete[] dynAllocatedMemory;
	}
	dynAllocatedMemory = new char[strlen(src) + 1];
	strcpy(dynAllocatedMemory, src);
}

