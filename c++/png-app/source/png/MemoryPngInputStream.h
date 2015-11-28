/*
 * MemoryPngInputStream.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef MEMORYPNGINPUTSTREAM_H_
#define MEMORYPNGINPUTSTREAM_H_

#include <libpng16/pngconf.h>

#include "PngInputStream.h"

class MemoryPngInputStream: public PngInputStream {
public:
	MemoryPngInputStream(const png_bytep bufferWithPng,
			const png_size_t nrOfBytes);
	virtual ~MemoryPngInputStream();
	void readData(png_bytep outBytesPtr, png_size_t byteCountToRead);
	void reset();
private:
	const png_bytep bufferWithPng;
	const png_size_t nrOfBytes;
	png_size_t bytesRead;
};

#endif /* MEMORYPNGINPUTSTREAM_H_ */
