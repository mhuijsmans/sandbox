/*
 * PngInputStream.cpp
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#include "PngInputStream.h"
#include "PngException.h"

PngInputStream::PngInputStream() {
	// TODO Auto-generated constructor stub

}

PngInputStream::~PngInputStream() {
	// TODO Auto-generated destructor stub
}

void PngInputStream::readData(png_bytep outBytesPtr,
	png_size_t byteCountToRead) {
	throw PngException("Not Implemented");
}

