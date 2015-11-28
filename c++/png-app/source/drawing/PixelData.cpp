#include <stdexcept>
#include <cstring>

#include "PixelData.h"

#define ASSERT_TRUE(a,b) { if (!a) throw std::runtime_error(b); }

#define NR_OF_COLORS 3
#define NR_OF_BYTES_PER_COLOR 2

PixelData::PixelData(const uint32_t width_, const uint32_t height_,
		const uint32_t bitDepth_) :
		width(width_), height(height_), bitDepth(bitDepth_), pixelData(0) {
	ASSERT_TRUE(bitDepth==16, "Only bitDepth 16 supported')");
	uint32_t nrOfElements = width * height * NR_OF_COLORS;
	pixelData = new uint16_t[nrOfElements];
	memset(pixelData, 0, GetNrOfBytes());
}

PixelData::~PixelData() {
	if (pixelData) {
		delete[] pixelData;
	}
}

uint32_t PixelData::GetNrOfBytes() {
	return width * height * NR_OF_COLORS * NR_OF_BYTES_PER_COLOR;
}

