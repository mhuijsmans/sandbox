/*
 * AssertUtil.cpp
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#include <stdexcept>

#include "AssertUtil.h"

#define ASSERT_EQUALS(a,b,c) { if (a!=b) throw std::runtime_error(c); }
#define ASSERT_NOT_NULL(a,b) { if (a==NULL) throw std::runtime_error(b); }

void AssertUtil::equals(PngReader &rdr1, PngReader &rdr2) {
	ASSERT_EQUALS(rdr1.getWidth(), rdr2.getWidth(), "Width is different");
	ASSERT_EQUALS(rdr1.getHeight(), rdr2.getHeight(), "Height is different");
	ASSERT_EQUALS(rdr1.getColorType(), rdr2.getColorType(), "Colortype is different");
	ASSERT_EQUALS(rdr1.getBitDepth(), rdr2.getBitDepth(), "Bitdepth is different");
	ASSERT_EQUALS(rdr1.getNrOfBytesPerRow(), rdr2.getNrOfBytesPerRow(), "NrOfBytesPerRow is different");

	png_bytep * d1 = rdr1.getPixelDataRowPointers();
	png_bytep * d2 = rdr2.getPixelDataRowPointers();
	ASSERT_NOT_NULL(d1, "D1 is null");
	ASSERT_NOT_NULL(d2, "D2 is null");
	for(int i=0; i<rdr1.getHeight(); i++) {
		png_bytep row1 = d1[i];
		png_bytep row2 = d2[i];
		for(int j=0; j<rdr1.getNrOfBytesPerRow(); j++) {
			ASSERT_EQUALS(row1[j], row2[j], "Difference in a row");
		}
	}
}

