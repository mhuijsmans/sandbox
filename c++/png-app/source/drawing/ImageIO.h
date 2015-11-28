/*
 * Draw.h
 *
 *  Created on: Sep 6, 2014
 *      Author: martien
 */

#ifndef DRAW_H_
#define DRAW_H_

#include <inttypes.h>

//namespace mahu {
//
//namespace draw {

class Color {
public:
	uint16_t red;
	uint16_t green;
	uint16_t blue;
};

class Colors {
public:
	static Color black;
	static Color white;
	static Color red;
	static Color green;
	static Color blue;
private:
	Colors();
};

// bitdepth = 16
enum BufferType {
	interleaved, planar_rowbased, planar_columnbased
};

class ImageIO {
public:
	ImageIO(uint16_t *buffer, uint16_t width, uint16_t height,
			const BufferType bufferType);
	virtual ~ImageIO();

	uint32_t GetNrOfPixels();
	uint32_t GetNrOfMatchingPixels(const Color &color);

	// topleft is 0,0
	// x-axis is from left top right (0-n)
	// y-axis is from top to bottom (0-n)
	// so TF is 0,0
	// and BL is 0,n
	void Fill(const Color & color) { Fill(width,height,color); }
	void Fill(int width, int height, const Color & color);
	void DrawRectangle(int x, int y, int width, int height, const Color & color);
	void DrawFilledRectangle(int x, int y, int width, int height, const Color & color);
	void DrawLine(int x1, int y1, int x2, int y2, const Color & color);
	void SetPixel(int x, int y, const Color & color);
	void GetPixel(int x, int y, Color & color);
private:
	uint16_t *buffer;
	const uint16_t width;
	const uint16_t height;
	const uint32_t nrOfPixelElements;
	const BufferType bufferType;
};

//}
//;
//
//}
//;

#endif /* DRAW_H_ */
