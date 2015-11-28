/*
 * MemoryPngInputStream.cpp
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#include <string.h>
#include "MemoryPngInputStream.h"
#include "PngException.h"

MemoryPngInputStream::MemoryPngInputStream(const png_bytep bufferWithPng_,
		const png_size_t nrOfBytes_) :
		bufferWithPng(bufferWithPng_), nrOfBytes(nrOfBytes_), bytesRead(0) {
}

MemoryPngInputStream::~MemoryPngInputStream() {
	// TODO Auto-generated destructor stub
}

void MemoryPngInputStream::readData(png_bytep outBytesPtr,
		png_size_t nrOfBytesToRead) {
	if (bytesRead + nrOfBytesToRead > nrOfBytes) {
		throw PngException("Trying to read beyond end of buffer");
	}
	memcpy(outBytesPtr, bufferWithPng+bytesRead, nrOfBytesToRead);
	bytesRead += nrOfBytesToRead;
}

void MemoryPngInputStream::reset() {
	bytesRead = 0;
}

