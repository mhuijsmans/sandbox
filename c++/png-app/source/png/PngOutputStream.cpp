#include "PngOutputStream.h"
#include "PngException.h"

PngOutputStream::PngOutputStream() {
}
PngOutputStream::~PngOutputStream() {
}

void PngOutputStream::writeData(const png_bytep dataToWrite,
		const png_size_t nrOfBytesToWrite) {
	throw PngException("Not Implemented");
}

void PngOutputStream::flush() {
	throw PngException("Not Implemented");
}
