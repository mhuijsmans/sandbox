#ifndef PngReader_H_
#define PngReader_H_

#include <memory>
#include <stdint.h>

#include <libpng16/pngconf.h>
#include "PngInputStream.h"

class PngReaderImpl;

class PngReader {
public:
	PngReader();
	~PngReader();

	// Default, pixel data memory is allocated/owned by pnglib (the used library).
	// By calling this function, memory allocation is done by this class.
	void readFromInputstream(PngInputStream& pngInputStream, bool allocateMemory=false);

	// Width and height are in number of pixels
	png_uint_32 getWidth();
	png_uint_32 getHeight();
	// bitdepth is typically 8,16 bits (but can also be 1,2,4).
	png_byte getBitDepth();

	// ColorType describes the interpretation of the pixel data
	// For example, color type=2, => each pixel is an R,G,B triple and allowed bitDepth=8,16
	// For details, see section 4 of http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html
	png_byte getColorType();

	// png pixel data as a set of row pointers. getHeight() gives number of rows.
	// row_pointer ::= byte*
	png_bytep *getPixelDataRowPointers();
	unsigned int getNrOfBytesPerRow();
	std::shared_ptr<uint8_t>& getMemoryBlock();

    // Return image properties as string
    std::string GetImageProperties();
private:
	// To keep the header as simple as possible, details are in PngReaderImpl.
	PngReaderImpl *impl;
};

#endif
