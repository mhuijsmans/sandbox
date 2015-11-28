#include <libpng16/png.h>
#include <csetjmp>
#include <cstdio>
#include <iostream>
#include <sstream>

#include "PngReader.h"
#include "PngException.h"

#define PNG_MAGIC_HEADER_SIZE 8
#define ASSERT_IMPL_SET { if (!impl) throw PngException("Use of method is only allowed after data has been read"); }

class PngReaderImpl {
public:
	PngReaderImpl(PngInputStream& pngInputStream);
	virtual ~PngReaderImpl();

	void readFromInputstream(bool allocateMemory);

	png_uint_32 getWidth() {
		return width;
	}
	png_uint_32 getHeight() {
		return height;
	}
	png_byte getBitDepth() {
		return bitDepth;
	}
	png_byte getColorType() {
		return colorType;
	}
	png_bytep * getPixelDataRowPointers() {
		return (!isMemoryAllocatedThisClass) ? rowPointers : rowPointers2.get();
	}
	unsigned int getNrOfBytesPerRow() {
		return nrOfBytesPerRow;
	}
	std::shared_ptr<uint8_t>& getMemoryBlock();
    std::string GetImageProperties();
private:
	PngInputStream& pngInputStream;

	png_uint_32 width;
	png_uint_32 height;
	unsigned int nrOfBytesPerRow;
	png_byte colorType;
	png_byte bitDepth;
	png_uint_32 filterType;
	png_uint_32 compresssionType;
	png_uint_32 interlaceType;
	png_structp pngSessionData;
	png_infop pngInfo;
	// pixel data
	// memory may be allocated / released by libpng.
	png_bytepp rowPointers;
	bool isMemoryAllocatedThisClass;
	std::shared_ptr<uint8_t> memoryBlock;
	std::shared_ptr<png_bytep> rowPointers2;

	void deletePngLibResources();
	void printImageProperties();

	void createReadAndInfoStruct();

	void readImageProperties();
	void setupInputSource();
	void informLibPngThatHeaderHasBeenRead();
	void setLimitsForWidthAndHeight();
	void prepareForReading();
	void setErrorHandlingWithSetjmp();
	void readPixelData();
	void readDataFromInputstream(png_bytep outBytes,
			png_size_t byteCountToRead);
	static void readDataFromInputstreamProxy(png_structp pngSessionData,
			png_bytep outBytes, png_size_t nrOfBytesToRead);
	void checkPngMagicHeader(const unsigned char* buffer);
	void informLibPngAboutInputSource();
	void readAndCheckPngMagicHeader();
};

PngReader::PngReader() :
		impl(NULL) {
}

PngReader::~PngReader() {
	if (impl) {
		delete impl;
	}
}

void PngReader::readFromInputstream(PngInputStream& pngInputStream,
		bool allocateMemory) {
	if (impl) {
		throw std::runtime_error("Method can be called only once");
	}
	impl = new PngReaderImpl(pngInputStream);
	impl->readFromInputstream(allocateMemory);
}

png_uint_32 PngReader::getWidth() {
	ASSERT_IMPL_SET;
	return impl->getWidth();
}
png_uint_32 PngReader::getHeight() {
	ASSERT_IMPL_SET;
	return impl->getHeight();
}
png_byte PngReader::getBitDepth() {
	ASSERT_IMPL_SET;
	return impl->getBitDepth();
}
png_byte PngReader::getColorType() {
	ASSERT_IMPL_SET;
	return impl->getColorType();
}
png_bytep * PngReader::getPixelDataRowPointers() {
	ASSERT_IMPL_SET;
	return impl->getPixelDataRowPointers();
}
unsigned int PngReader::getNrOfBytesPerRow() {
	ASSERT_IMPL_SET;
	return impl->getNrOfBytesPerRow();
}

std::shared_ptr<uint8_t>& PngReader::getMemoryBlock() {
	ASSERT_IMPL_SET;
	return impl->getMemoryBlock();
}

std::string PngReader::GetImageProperties() {
    ASSERT_IMPL_SET;
    return impl->GetImageProperties();
}

// #######################################################

PngReaderImpl::PngReaderImpl(PngInputStream& pngInputStream_) :
		pngInputStream(pngInputStream_), width(0), height(0), colorType(0), bitDepth(
				0), nrOfBytesPerRow(0), pngSessionData(NULL), pngInfo(
		NULL), filterType(0), compresssionType(0), interlaceType(0), rowPointers(
				0), isMemoryAllocatedThisClass(false),
		memoryBlock(new uint8_t[0], []( uint8_t *p ) {delete[] p;}),
		rowPointers2(new png_bytep[0], []( png_bytep *p ) {delete[] p;}) {
}

PngReaderImpl::~PngReaderImpl() {
	deletePngLibResources();
}

void PngReaderImpl::readFromInputstream(bool allocateMemory) {
	isMemoryAllocatedThisClass = allocateMemory;
	readAndCheckPngMagicHeader();
	createReadAndInfoStruct();
	setErrorHandlingWithSetjmp();
	informLibPngAboutInputSource();
	informLibPngThatHeaderHasBeenRead();
	setLimitsForWidthAndHeight();
	prepareForReading();
	readPixelData();
	// printImageProperties();
}

std::shared_ptr<uint8_t>& PngReaderImpl::getMemoryBlock() {
	return memoryBlock;
}

void PngReaderImpl::createReadAndInfoStruct() {
	// section 3.1 of
	// http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf
	png_voidp userErrorPtr = NULL; // use default handler
	png_error_ptr pngErrorFunction = NULL; // use default handler
	png_error_ptr pngWarningFunction = NULL; // use default handler
	pngSessionData = png_create_read_struct(PNG_LIBPNG_VER_STRING, userErrorPtr,
			pngErrorFunction, pngWarningFunction);
	if (!pngSessionData) {
		throw PngException("Failed to read png_structp");
	}
	//
	pngInfo = png_create_info_struct(pngSessionData);
	if (!pngInfo) {
		throw PngException("Failed to read png_infop");
	}
}

void PngReaderImpl::informLibPngAboutInputSource() {
	png_voidp io_ptr = (png_voidp) (this);
	png_rw_ptr readDataFunctionPtr =
			static_cast<png_rw_ptr>(&PngReaderImpl::readDataFromInputstreamProxy);
	png_set_read_fn(pngSessionData, io_ptr, readDataFunctionPtr);
}

void PngReaderImpl::informLibPngThatHeaderHasBeenRead() {
	// Signature from the beginning has been read. LibPng must be informed
	// See section: Setup
	// at: http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf
	png_set_sig_bytes(pngSessionData, PNG_MAGIC_HEADER_SIZE);
}

void PngReaderImpl::setLimitsForWidthAndHeight() {
	// Manual indicates that callback (e.g. chunks can be set).
	// Sofar I am not interested in chunks.
	// Png spec allows for width/height upto 2^31-1
	// Set our limits
	unsigned int widthMax = 5000;
	unsigned int heightMax = 5000;
	png_set_user_limits(pngSessionData, widthMax, heightMax);
}

void PngReaderImpl::readImageProperties() {
	width = png_get_image_width(pngSessionData, pngInfo);
	height = png_get_image_height(pngSessionData, pngInfo);
	colorType = png_get_color_type(pngSessionData, pngInfo);
	bitDepth = png_get_bit_depth(pngSessionData, pngInfo);
	filterType = png_get_filter_type(pngSessionData, pngInfo);
	compresssionType = png_get_compression_type(pngSessionData, pngInfo);
	interlaceType = png_get_interlace_type(pngSessionData, pngInfo);
}

void PngReaderImpl::prepareForReading() {
	// High Level track
	// http://refspecs.linuxfoundation.org/LSB_3.1.1/LSB-Desktop-generic/LSB-Desktop-generic/libpng12.png.read.png.1.html
	if (!isMemoryAllocatedThisClass) {
		png_read_png(pngSessionData, pngInfo,
		PNG_TRANSFORM_IDENTITY, NULL);
	} else {
		png_read_info(pngSessionData, pngInfo);
	}
	//
	int bitDepth_ = 0;
	int colorType_ = 0;
	// std::cout << "png_get_IHDR" << std::endl;
	png_get_IHDR(pngSessionData, pngInfo, &width, &height, &bitDepth_,
			&colorType_, NULL, NULL, NULL);
	bitDepth = bitDepth_;
	colorType = colorType_;
}

void PngReaderImpl::readPixelData() {
	nrOfBytesPerRow = png_get_rowbytes(pngSessionData, pngInfo);
	if (!isMemoryAllocatedThisClass) {
		// Note that memory is owned by libPng
		// png_get_rows() shall return an array of pointers to the pixel data for each row of the image
		rowPointers = png_get_rows(pngSessionData, pngInfo);
	} else {
		// http://examples.oreilly.de/english_examples/gff/CDROM/SOFTWARE/SOURCE/LIBPNG/EXAMPLE.C
		// Allocate memory & set the row pointers into the allocated memory.
		uint32_t nrOfBytes = nrOfBytesPerRow * height;
		memoryBlock.reset(new uint8_t[nrOfBytes]);
		rowPointers2.reset(new png_bytep[height]);
		uint8_t *ptr = memoryBlock.get();
		png_bytepp rows = rowPointers2.get();
		for (int i = 0; i < height; i++) {
			rows[i] = ptr;
			ptr += nrOfBytesPerRow;
		}
		// Ask libpng to write the data into the allocated memory.
		// See http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf, section 3.3.
		png_read_image(pngSessionData, rows);
	}
}

void PngReaderImpl::readDataFromInputstream(png_bytep outBytesPtr,
		png_size_t nrOfBytesToRead) {
	pngInputStream.readData(outBytesPtr, nrOfBytesToRead);
}

void PngReaderImpl::readDataFromInputstreamProxy(png_structp pngSessionData,
		png_bytep outBytesPtr, png_size_t nrOfBytesToRead) {
	PngReaderImpl *pngReaderImpl = (PngReaderImpl *) png_get_io_ptr(
			pngSessionData);
	if (pngReaderImpl == NULL) {
		throw PngException("Invalid input, png_get_io_ptr returned NULL");
	}
// -> replace with your own data source interface
	pngReaderImpl->readDataFromInputstream(outBytesPtr, nrOfBytesToRead);
}

void PngReaderImpl::readAndCheckPngMagicHeader() {
	unsigned char header[PNG_MAGIC_HEADER_SIZE];
	pngInputStream.readData(header, (png_size_t) PNG_MAGIC_HEADER_SIZE);
	checkPngMagicHeader(header);
}

void PngReaderImpl::checkPngMagicHeader(const unsigned char* buffer) {
	if (png_sig_cmp(buffer, 0, PNG_MAGIC_HEADER_SIZE)) {
		throw PngException("PNG does not contain the required PNG header");
	}
}

void PngReaderImpl::deletePngLibResources() {
	// Free the memory associated with the pngSessionData and pngInfo.
	// See section 3.1 of
	// http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf
	// If rowPointers are allocated by libpng, they are also released by libpng.
	if (pngSessionData != NULL && pngInfo != NULL) {
		png_destroy_read_struct(&pngSessionData, &pngInfo, (png_infopp) NULL);
	} else if (pngSessionData != NULL) {
		png_destroy_read_struct(&pngSessionData, (png_infopp) NULL,
				(png_infopp) NULL);
	}
	pngSessionData = NULL;
	pngInfo = NULL;
}

void PngReaderImpl::setErrorHandlingWithSetjmp() {
	if (setjmp(png_jmpbuf(pngSessionData))) {
		// RAII is applied to release resources.
		// http://en.wikipedia.org/wiki/Resource_Acquisition_Is_Initialization
		throw PngException("LibPng reported error");
	}
}

void PngReaderImpl::printImageProperties() {
    // http://www.w3.org/TR/PNG-Chunks.html
    // color_type = 2 => each pixel is RGB triplet
    // if color_type=2, bit-depth = 8/16
    std::cout << GetImageProperties() << std::endl;
}

std::string PngReaderImpl::GetImageProperties() {
    // http://www.w3.org/TR/PNG-Chunks.html
    // color_type = 2 => each pixel is RGB triplet
    // if color_type=2, bit-depth = 8/16
    png_uint_32 colorType_ = colorType;
    png_uint_32 bitDepth_ = bitDepth;
    std::stringstream ss;
    ss << "Image information width=" << width << ", height=" << height
            << ", colorType=" << colorType_ << ", bitDepth=" << bitDepth_
            << ", filterType=" << filterType << ", compressionType="
            << compresssionType << ", interlaceType=" << interlaceType
            << ", nrOfBytesPerRow=" << nrOfBytesPerRow;
    return ss.str();
}