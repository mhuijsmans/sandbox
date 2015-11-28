/*
 * Png.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef PNGDATA_H_
#define PNGDATA_H_

#include <cstdint>
#include <libpng16/pngconf.h>

class PngData {
public:
	PngData(const png_uint_32 width, const png_uint_32 height,
			const png_byte bit_depth, const png_byte color_type,
			png_bytep * row_pointers);
	virtual ~PngData();

	uint32_t getNrOfBytesPixelData() const;

	const png_uint_32 width;
	const png_uint_32 height;
	const png_byte bitDepth;
	const png_byte colorType;
	png_bytep * rowPointers;
};

#endif /* PNGDATA_H_ */
