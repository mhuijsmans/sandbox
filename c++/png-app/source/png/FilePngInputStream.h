/*
 * FilePngInputStream.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef FILEPNGINPUTSTREAM_H_
#define FILEPNGINPUTSTREAM_H_

#include <libpng16/pngconf.h>
#include "PngInputStream.h"

class FilePngImpl;

class FilePngInputStream: public PngInputStream {
public:
	FilePngInputStream(const char *fileName);
	virtual ~FilePngInputStream();
	void readData(png_bytep outBytesPtr, png_size_t byteCountToRead);
private:
	FilePngImpl* file;
};

#endif /* FILEPNGINPUTSTREAM_H_ */
