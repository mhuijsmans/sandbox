/*
 * AssertUtil.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef ASSERTUTIL_H_
#define ASSERTUTIL_H_

#include "png/PngReader.h"

class AssertUtil {
public:
	static void equals(PngReader &rdr1, PngReader &rdr2);
private:
	AssertUtil() {}
	virtual ~AssertUtil() {}
};

#endif /* ASSERTUTIL_H_ */
