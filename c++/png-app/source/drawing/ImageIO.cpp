/*

 * Draw.cpp
 *
 *  Created on: Sep 6, 2014
 *      Author: martien
 */

#include <cstdlib>
#include <iostream>
#include <stdexcept>

#include "ImageIO.h"

//namespace mahu {
//namespace draw {

//using namespace mahu::draw;

#define NR_OF_COLORS 3
#define DEBUG false
// #define NR_OF_BYTES_PER_COLOR 2

#define ASSERT_LESS(a,b,c) { if (a>=b) throw std::runtime_error(c); }
#define ASSERT_IN_RANGE(a,b,c) { if (!(a>=0 && a<b)) throw std::runtime_error(c); }

Color Colors::black = { 0xff, 0xff, 0xff };
Color Colors::white = { 0, 0, 0 };
Color Colors::red = { 0xff, 0, 0 };
Color Colors::green = { 0, 0xff, 0 };
Color Colors::blue = { 0, 0, 0xff };

class CalculateOffset {
public:
	CalculateOffset(const int x, const int y, const BufferType bufferType,
			const int width, const int height, const int nrOfPixelElements) :
			offsetR(0), offsetG(0), offsetB(0) {
		switch (bufferType) {
		case interleaved: {
			offsetR = ((y * width) + x) * NR_OF_COLORS;
			offsetG = offsetR + 1;
			offsetB = offsetG + 1;
			break;
		}
		case planar_rowbased: {
			int nrOfElementsPerColor = width * height;
			offsetR = ((y * width) + x);
			offsetG = offsetR + nrOfElementsPerColor;
			offsetB = offsetG + nrOfElementsPerColor;
			break;
		}
		case planar_columnbased: {
			throw std::runtime_error("To be tested");
			int nrOfElementsPerColor = width * height;
			offsetR = ((x * height) + y);
			offsetG = offsetR + nrOfElementsPerColor;
			offsetB = offsetG + nrOfElementsPerColor;
			break;
		}
		default:
			throw std::runtime_error("Implemented error");
		}
		if (DEBUG) {
			std::cout << "PixelOffset " << ", red: " << offsetR << ", green: "
					<< offsetG << ", blue: " << offsetB << std::endl;
			ASSERT_IN_RANGE(offsetR, nrOfPixelElements,
					"OffsetR trying to read/write outside buffer");
			ASSERT_IN_RANGE(offsetG, nrOfPixelElements,
					"OffsetG trying to read/writ outside buffer");
			ASSERT_IN_RANGE(offsetB, nrOfPixelElements,
					"OffsetB trying to read/writ outside buffer");
		}
	}
	int offsetR;
	int offsetG;
	int offsetB;
};

ImageIO::ImageIO(uint16_t *buffer_, uint16_t width_, uint16_t height_,
		const BufferType bufferType_) :
		buffer(buffer_), width(width_), height(height_), nrOfPixelElements(
				width_ * height_ * NR_OF_COLORS), bufferType(bufferType_) {
}

ImageIO::~ImageIO() {
}

uint32_t ImageIO::GetNrOfPixels() {
	uint32_t nrOfPixels = width * height;
	std::cout << "GetNrOfPixels returns: " << nrOfPixels << std::endl;
	return nrOfPixels;
}

uint32_t ImageIO::GetNrOfMatchingPixels(const Color &color) {
	uint32_t cntr = 0;
	Color tmp;
	for (int x = 0; x < width; x++) {
		for (int y = 0; y < height; y++) {
			GetPixel(x, y, tmp);
			if (DEBUG) {
				std::cout << "Color " << ", red: " << tmp.red << ", green: "
						<< tmp.green << ", blue: " << tmp.blue << std::endl;
			}
			if (tmp.red == color.red && tmp.green == color.green
					&& tmp.blue == color.blue) {
				cntr++;
			}
		}
	}
	std::cout << "GetNrOfMatchingPixels returns: " << cntr << std::endl;
	return cntr;
}

void ImageIO::DrawRectangle(const int x, const int y, const int width,
		const int height, const Color & color) {
// 2 horizontal lines
	DrawLine(x, y, x + width - 1, y, color);
	DrawLine(x, y + height - 1, x + width - 1, y + height - 1, color);
// vertical lines
	DrawLine(x, y, x, y + height - 1, color);
	DrawLine(x + width - 1, y, x + width - 1, y + height - 1, color);
}

void ImageIO::DrawFilledRectangle(const int x, const int y, const int width,
		const int height, const Color & color) {
	for (int t = y; t < y + height; t++) {
		DrawLine(x, t, x + width - 1, t, color);
	}
}

void ImageIO::Fill(int width, int height, const Color & color) {
	for (int y = 0; y < height; y++) {
		DrawLine(0, y, width - 1, y, color);
	}
}

void ImageIO::DrawLine(const int x1, const int y1, const int x2, const int y2,
		const Color & color) {
	if (DEBUG) {
		std::cout << "DrawLine: [" << x1 << "," << y1 << ":][" << x2 << ","
				<< y2 << "]" << std::endl;
	}
	int x = x1;
	int y = y1;
	if (x1 == x2) {
		// vertical line
		while (y <= y2) {
			SetPixel(x, y, color);
			y++;
		}
		return;
	}
	if (y1 == y2) {
		// horizontal line
		while (x <= x2) {
			SetPixel(x, y, color);
			x++;
		}
		return;
	}

	throw std::runtime_error("Only horizontal & vertical line implemented");
}

void ImageIO::SetPixel(int x, int y, const Color & color) {
	if (DEBUG) {
		std::cout << "SetPixel: " << x << "," << y << std::endl;
	}
	CalculateOffset offset(x, y, bufferType, width, height, nrOfPixelElements);

	buffer[offset.offsetR] = color.red;
	buffer[offset.offsetG] = color.green;
	buffer[offset.offsetB] = color.blue;
}

void ImageIO::GetPixel(int x, int y, Color & color) {
	if (DEBUG) {
		std::cout << "GetPixel: " << x << "," << y << std::endl;
	}
	CalculateOffset offset(x, y, bufferType, width, height, nrOfPixelElements);

	color.red = buffer[offset.offsetR];
	color.green = buffer[offset.offsetG];
	color.blue = buffer[offset.offsetB];
}

// }

//}

