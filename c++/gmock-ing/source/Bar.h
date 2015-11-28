/*
 * Bar.h
 *
 *  Created on: Dec 29, 2014
 *      Author: 310160231
 */

#ifndef SOURCE_BAR_H_
#define SOURCE_BAR_H_

#include "Foo.h"

class Bar {
public:
	Bar(Foo &foo);
	virtual ~Bar();

	int doOne();
	int doTwo();
    int OneArgument(int);
private:
	Foo &foo;
};

#endif /* SOURCE_BAR_H_ */
