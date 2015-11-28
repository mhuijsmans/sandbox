#include <libpng16/pngconf.h>
#include <stddef.h>
#include <iostream>
#include <vector>

#include "gtest/gtest.h"
#include "png/FilePngInputStream.h"
#include "png/MemoryPngInputStream.h"
#include "png/MemoryPngOutputStream.h"
#include "png/PngData.h"
#include "png/PngReader.h"
#include "png/PngWriter.h"
#include "util/Chrono.h"
#include "util/IOUtils.h"
#include "AssertUtil.h"

static const char *IMAGE1 = "test/resources/image1.png";
static const size_t COLORTYPE_RGB = 2;

// IMPORTANT: not that PNG pixel data is stored in network order (which is big endian).

TEST(Test1, pngReadsFromFileMemoryAllocatedByLibPng) {
	std::cout
			<< "########## Image file read (using high-level PNG API) via inputstream; memory allocated by LibPng"
			<< std::endl;
	Chrono chrono;
	PngReader pngReader;
	FilePngInputStream filePngInputStream(IMAGE1);
	pngReader.readFromInputstream(filePngInputStream);
	std::cout << "Image read from input stream in " << chrono.elapsedTimeInMs()
			<< " ms" << std::endl;

	EXPECT_EQ(8, pngReader.getBitDepth());
	EXPECT_EQ(COLORTYPE_RGB, pngReader.getColorType()); //
	const size_t expectedNrOfBytes = pngReader.getWidth() * 3 /*rgb*/
	* 1 /* # bytes/color*/;
	EXPECT_EQ(expectedNrOfBytes, pngReader.getNrOfBytesPerRow());
}

TEST(Test1, pngReadsFromFileMemoryAllocatedByPngReader) {
	std::cout
			<< "########## Image file read (using high-level PNG API) via inputstream;  memory allocated by PngReader"
			<< std::endl;
	Chrono chrono;
	IOUtils ioUtuls;

	ioUtuls.readFile(IMAGE1);
	MemoryPngInputStream memoryInputstream(
			(const png_bytep) ioUtuls.getBuffer(),
			(const png_size_t) ioUtuls.getLength());
	std::cout << "Image read info memory in " << chrono.elapsedTimeInMs()
			<< " ms" << std::endl;

	chrono.reset();
	PngReader pngReader1;
	{
		pngReader1.readFromInputstream(memoryInputstream);
		std::cout << "Image read from input stream in "
				<< chrono.elapsedTimeInMs() << " ms" << std::endl;
	}

	chrono.reset();
	PngReader pngReader2;
	{
		memoryInputstream.reset();
		pngReader2.readFromInputstream(memoryInputstream, true);
		std::cout << "Image read from input stream in "
				<< chrono.elapsedTimeInMs() << " ms" << std::endl;
	}

	AssertUtil::equals(pngReader1, pngReader2);
}

TEST(Test1, pngReadsFromMemory) {
	std::cout << "########## Image memory read" << std::endl;
	Chrono chrono;
	IOUtils ioUtuls;
	ioUtuls.readFile(IMAGE1);
	MemoryPngInputStream memoryInputstream(
			(const png_bytep) ioUtuls.getBuffer(),
			(const png_size_t) ioUtuls.getLength());
	std::cout << "Image read info memory in " << chrono.elapsedTimeInMs()
			<< " ms" << std::endl;

	chrono.reset();
	PngReader pngReaderFromMemory;
	pngReaderFromMemory.readFromInputstream(memoryInputstream);
	std::cout << "Image read memory in " << chrono.elapsedTimeInMs() << " ms"
			<< std::endl;

	// Now compare that the data read first in memory produces the same result
	PngReader pngReaderFromFile;
	FilePngInputStream filePngInputStream(IMAGE1);
	pngReaderFromFile.readFromInputstream(filePngInputStream);
	AssertUtil::equals(pngReaderFromFile, pngReaderFromMemory);
}

TEST(Test1, pngWriteDataToMemory) {
	std::cout << "########## Image memory write" << std::endl;

	PngReader pngReaderFromFile;
	FilePngInputStream filePngInputStream(IMAGE1);
	pngReaderFromFile.readFromInputstream(filePngInputStream);

	Chrono chrono;
	PngData pngData(pngReaderFromFile.getWidth(), pngReaderFromFile.getHeight(),
			pngReaderFromFile.getBitDepth(), pngReaderFromFile.getColorType(),
			pngReaderFromFile.getPixelDataRowPointers());
	PngWriter writer(pngData);
	MemoryPngOutputStream memOs;
	writer.writePngToOutputStream(memOs);
	std::cout << "Image write png to memory in " << chrono.elapsedTimeInMs()
			<< " ms, nrOfBytes=" << memOs.getNrOfBytes() << std::endl;

	// Read the data written to memory, and check against the original
	MemoryPngInputStream memoryInputstream(memOs.getBuffer().data(),
			memOs.getNrOfBytes());
	PngReader pngReaderFromMemory;
	pngReaderFromMemory.readFromInputstream(memoryInputstream);

	AssertUtil::equals(pngReaderFromFile, pngReaderFromMemory);
}

TEST(Test1, pngWriteDataToMemoryDefaultAndPrealloc) {
	std::cout << "########## Image memory write" << std::endl;

	PngReader pngReaderFromFile;
	FilePngInputStream filePngInputStream(IMAGE1);
	pngReaderFromFile.readFromInputstream(filePngInputStream);

	Chrono chrono;
	PngData pngData(pngReaderFromFile.getWidth(), pngReaderFromFile.getHeight(),
			pngReaderFromFile.getBitDepth(), pngReaderFromFile.getColorType(),
			pngReaderFromFile.getPixelDataRowPointers());
	std::cout << "Number of bytes pixelData=" << pngData.getNrOfBytesPixelData() << std::endl;

	MemoryPngOutputStream memOs1;
	{
		PngWriter writer(pngData);
		writer.writePngToOutputStream(memOs1);
		std::cout << "Image write png to memory (no pre-alloc) in " << chrono.elapsedTimeInMs()
				<< " ms, nrOfBytes=" << memOs1.getNrOfBytes() << std::endl;
	}

	{
		PngWriter writer(pngData);
		const uint32_t estimatedPngHeaderSize= 512;
		uint32_t outputBufferSize = pngData.getNrOfBytesPixelData() + estimatedPngHeaderSize;
		MemoryPngOutputStream memOs2(outputBufferSize);
		writer.writePngToOutputStream(memOs2);
		std::cout << "Image write png to memory (pre-alloc) in " << chrono.elapsedTimeInMs()
				<< " ms, nrOfBytes=" << memOs2.getNrOfBytes() << std::endl;

		EXPECT_EQ(memOs1.getNrOfBytes(), memOs2.getNrOfBytes());
	}
}
