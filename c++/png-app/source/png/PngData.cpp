#include "PngData.h"
#include "PngException.h"

PngData::PngData(const png_uint_32 width_, const png_uint_32 height_,
		const png_byte bit_depth_, const png_byte color_type_,
		png_bytep * row_pointers_) :
		width(width_), height(height_), bitDepth(bit_depth_), colorType(
				color_type_), rowPointers(row_pointers_) {
}

PngData::~PngData() {
}

uint32_t PngData::getNrOfBytesPixelData() const {
	// ref: http://www.w3.org/TR/PNG-Chunks.html
	switch(colorType) {
	case 0: // greyscale
		return width * height * (bitDepth<=8?1:2) * 1;
	case 2: // RGB
		return width * height * (bitDepth<=8?1:2) * 3;
	default:
		throw new PngException("Only colorTypes 0,2 are supported");
	}
}

