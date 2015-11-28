#include <iostream>

#include "MemoryPngOutputStream.h"
#include "PngException.h"

MemoryPngOutputStream::MemoryPngOutputStream() :
		buffer() {
}

MemoryPngOutputStream::MemoryPngOutputStream(uint32_t n) :
		buffer() {
	if (n > 0) {
		buffer.reserve(n);
	}
}

MemoryPngOutputStream::~MemoryPngOutputStream() {
}

void MemoryPngOutputStream::writeData(const png_bytep dataToWrite,
		const png_size_t nrOfBytesToWrite) {
	buffer.insert(buffer.end(), dataToWrite, dataToWrite + nrOfBytesToWrite);
}

