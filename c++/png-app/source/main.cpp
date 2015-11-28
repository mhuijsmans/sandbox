#include <libpng16/pngconf.h>
#include <iostream>

#include "png/FilePngInputStream.h"
#include "png/MemoryPngInputStream.h"
#include "png/MemoryPngOutputStream.h"
#include "png/PngReader.h"
#include "png/PngWriter.h"
#include "util/Chrono.h"
#include "util/IOUtils.h"

int main(int argc, char **argv) {
	if (argc < 2) {
		std::cout << "Usage: pgn-app png-in" << std::endl;
		return 1;
	}

	Chrono chrono;

	chrono.reset();
	IOUtils png;
	png.readFile(argv[1]);
	MemoryPngInputStream memoryInputstream((const png_bytep) png.getBuffer(),
			(const png_size_t) png.getLength());
	std::cout << "Image read info memory in " << chrono.elapsedTimeInMs()
			<< " ms" << std::endl;

	{
		chrono.reset();
		PngReader rdrMem;
		memoryInputstream.reset();
		rdrMem.readFromInputstream(memoryInputstream, true);
		std::cout << "Image read memory (own alloc)in "
				<< chrono.elapsedTimeInMs() << " ms" << std::endl;
	}

	chrono.reset();
	PngReader rdrMem;
	memoryInputstream.reset();
	rdrMem.readFromInputstream(memoryInputstream);
	std::cout << "Image read memory (pnglib alloc) in "
			<< chrono.elapsedTimeInMs() << " ms" << std::endl;

	PngData pngData(rdrMem.getWidth(), rdrMem.getHeight(), rdrMem.getBitDepth(),
			rdrMem.getColorType(), rdrMem.getPixelDataRowPointers());
	std::cout << "Nr of bytes pixelData=" << pngData.getNrOfBytesPixelData()
			<< std::endl;

	// Loop to see if having a preallocated buffer works better. Answer: yes
	// Saving was 0-20 ms (on VM on Laptop: (windows7,i5-2410n@2.30 ghz, 8gb)
	const uint32_t estimatedPngHeaderSize = 1024 * 8;
	for (int j = 0; j < 10; j++) {
		for (uint32_t i = 0; i < 2; i++) {
			uint32_t outputBufferSize = pngData.getNrOfBytesPixelData()
					+ estimatedPngHeaderSize;
			MemoryPngOutputStream memOs(i * outputBufferSize);
			chrono.reset();
			PngWriter writer(pngData);
			writer.writePngToOutputStream(memOs);
			std::cout << "Iteration: " << i << ", image write png to memory in "
					<< chrono.elapsedTimeInMs() << " ms and has size "
					<< memOs.getNrOfBytes() << " bytes" << std::endl;
		}
	}

	return 0;
}
