/*
 * PngOutputStream.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef PNGOUTPUTSTREAM_H_
#define PNGOUTPUTSTREAM_H_

#include <libpng16/pngconf.h>

class PngOutputStream {
public:
	PngOutputStream() ;
	virtual ~PngOutputStream();

	virtual void writeData(const png_bytep dataToWrite, const png_size_t nrOfBytesToWrite);
	virtual void flush();
};

#endif /* PNGOUTPUTSTREAM_H_ */
