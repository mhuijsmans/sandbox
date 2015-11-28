/*
 * Png.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef PixelData_H_
#define PixelData_H_

#include <inttypes.h>

class PixelData {
public:
	PixelData(const uint32_t width, const uint32_t height,
			const uint32_t bitDepth);
	~PixelData();

	const uint32_t width;
	const uint32_t height;
	const uint32_t bitDepth;
	uint16_t * pixelData;

	uint32_t GetNrOfBytes();
};

#endif /* PixelData_H_ */
