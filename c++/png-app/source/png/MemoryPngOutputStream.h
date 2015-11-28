#ifndef MEMORYPNGOUTPUTSTREAM_H_
#define MEMORYPNGOUTPUTSTREAM_H_

#include <cstdint>
#include <vector>
#include <libpng16/pngconf.h>
#include "PngOutputStream.h"

class MemoryPngOutputStream : public PngOutputStream{
public:
	MemoryPngOutputStream() ;
	MemoryPngOutputStream(uint32_t n) ;
	virtual ~MemoryPngOutputStream();

	virtual void writeData(const png_bytep dataToWrite, const png_size_t nrOfBytesToWrite);

	unsigned int getNrOfBytes() { return buffer.size(); }
	std::vector<unsigned char> &getBuffer() { return buffer; }
private:
	std::vector<unsigned char> buffer;
};

#endif /* MEMORYPNGOUTPUTSTREAM_H_ */
