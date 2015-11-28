/*
 * PngInputStream.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef PNGINPUTSTREAM_H_
#define PNGINPUTSTREAM_H_

#include <libpng16/pngconf.h>

class PngInputStream {
public:
	PngInputStream() ;
	virtual ~PngInputStream();

	virtual void readData(png_bytep outBytesPtr,
		png_size_t byteCountToRead);
};

#endif /* PNGINPUTSTREAM_H_ */
