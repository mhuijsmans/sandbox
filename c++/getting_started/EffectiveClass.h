/*
 * EffectiveClass.h
 *
 *  Created on: Mar 14, 2014
 *      Author: martien
 */

#ifndef EFFECTIVECLASS_H_
#define EFFECTIVECLASS_H_

#include <string>

/*
 * This class includes all recommendation as described in Effective C++
 */

class EffectiveClass {
public:
	EffectiveClass();
	// Item 11: include a copy constructor (if you have dynamically allocated memory)
	EffectiveClass(const EffectiveClass &rhs);

	// Item 11: include a assignment operator
	EffectiveClass & operator=(const EffectiveClass &rhs);
	// item 14:  base class destructor are virtual
	virtual ~EffectiveClass();
private:
	// dynamically allocated memory
	// assumption is that it contains a 0 terminated string
	char *dynAllocatedMemory;

	// helper class
	void copy(const char *src);

	// rule 12 applies here
	std::string name;
};

#endif /* EFFECTIVECLASS_H_ */
