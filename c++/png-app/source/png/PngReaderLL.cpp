#include <libpng16/png.h>
#include <csetjmp>
#include <cstdio>
#include <iostream>
#include <stdexcept>

#include "PngException.h"

class PngInputStream;

#define PNG_MAGIC_HEADER_SIZE 8

class PngReaderLLImpl {
public:
	PngReaderLLImpl();
	virtual ~PngReaderLLImpl();

	void readFileLowLevelTrack(const char* fileName);

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
		return rowPointersLLT;
	}
	unsigned int getNrOfBytesPerRow() {
		return nrOfBytesPerRow;
	}
private:
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
	FILE* filePointer;
	// LowLevel track
	png_bytepp rowPointersLLT;

	void allocatePixelDataRowPointers();
	void deletePixelDataRowPointers();
	void deletePngLibResources();
	void closeFile();
	void printImageProperties();
	void openPngFile(const char* file_name);
	void createReadAndInfoStruct();
	void setupFileAsInputSource();
	void readImageProperties();
	void readPixelDataLowLevelTrack();
	void setupInputSource();
	void informLibPngThatHeaderHasBeenRead();
	void setLimitsForWidthAndHeight();
	void prepareForReading();
	void setErrorHandlingWithSetjmp();
	void readPixelDataHighLevelTrack();

	void checkPngMagicHeader(const unsigned char* buffer);
	void setupMemoryAsInputSource();
	void readAndCheckPngMagicHeader();
};
// #######################################################

PngReaderLLImpl::PngReaderLLImpl() :
		width(0), height(0), colorType(0), bitDepth(0), nrOfBytesPerRow(0), rowPointersLLT(
				0), pngSessionData(NULL), pngInfo(NULL), filePointer(0), filterType(
				0), compresssionType(0), interlaceType(0) {
}

PngReaderLLImpl::~PngReaderLLImpl() {
	deletePixelDataRowPointers();
	deletePngLibResources();
	closeFile();
}

void PngReaderLLImpl::readFileLowLevelTrack(const char* fileName) {
	openPngFile(fileName);
	createReadAndInfoStruct();

	setErrorHandlingWithSetjmp();

	// Default ligpng reads from the file input
	setupFileAsInputSource();
	// Signature from the beginning has been read. LibPng must be informed
	informLibPngThatHeaderHasBeenRead();

	// libpng manual indicates that callback can be set, e.g. for chunks.
	// Sofar I am not interested in chunks. So no code.

	// Png spec allows for width/height upto 2^31-1. Set our limits
	setLimitsForWidthAndHeight();

	// Low Level Track
	// Read all the png information upto the actual image data.
	// The read information includes the chunks.
	png_read_info(pngSessionData, pngInfo);
	readImageProperties();
	printImageProperties();

	readPixelDataLowLevelTrack();

	closeFile();
}

void PngReaderLLImpl::openPngFile(const char* file_name) {
	closeFile();
	// section 3.1 of
	// http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf
	filePointer = fopen(file_name, "rb");
	if (!filePointer) {
		throw PngException("Failed to open file for reading");
	}
	unsigned char header[PNG_MAGIC_HEADER_SIZE];
	fread(header, 1, PNG_MAGIC_HEADER_SIZE, filePointer);
	checkPngMagicHeader(header);
}

void PngReaderLLImpl::closeFile() {
	if (filePointer) {
		fclose(filePointer);
		filePointer = 0;
	}
}

void PngReaderLLImpl::createReadAndInfoStruct() {
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

void PngReaderLLImpl::setupFileAsInputSource() {
	png_init_io(pngSessionData, filePointer);
}

void PngReaderLLImpl::informLibPngThatHeaderHasBeenRead() {
	// Signature from the beginning has been read. LibPng must be informed
	// See section: Setup
	// at: http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf
	png_set_sig_bytes(pngSessionData, PNG_MAGIC_HEADER_SIZE);
}

void PngReaderLLImpl::setLimitsForWidthAndHeight() {
	// Manual indicates that callback (e.g. chunks can be set).
	// Sofar I am not interested in chunks.
	// Png spec allows for width/height upto 2^31-1
	// Set our limits
	unsigned int widthMax = 5000;
	unsigned int heightMax = 5000;
	png_set_user_limits(pngSessionData, widthMax, heightMax);
}

void PngReaderLLImpl::readImageProperties() {
	width = png_get_image_width(pngSessionData, pngInfo);
	height = png_get_image_height(pngSessionData, pngInfo);
	colorType = png_get_color_type(pngSessionData, pngInfo);
	bitDepth = png_get_bit_depth(pngSessionData, pngInfo);
	filterType = png_get_filter_type(pngSessionData, pngInfo);
	compresssionType = png_get_compression_type(pngSessionData, pngInfo);
	interlaceType = png_get_interlace_type(pngSessionData, pngInfo);
}

void PngReaderLLImpl::prepareForReading() {
	// High Level track
	std::cout << "png_read_png" << std::endl;
	// http://refspecs.linuxfoundation.org/LSB_3.1.1/LSB-Desktop-generic/LSB-Desktop-generic/libpng12.png.read.png.1.html
	png_read_png(pngSessionData, pngInfo,
	PNG_TRANSFORM_IDENTITY, NULL);
	//
	int bitDepth_ = 0;
	int colorType_ = 0;
	std::cout << "png_get_IHDR" << std::endl;
	png_get_IHDR(pngSessionData, pngInfo, &width, &height, &bitDepth_,
			&colorType_, NULL, NULL, NULL);
	bitDepth = bitDepth_;
	colorType = colorType_;
}

void PngReaderLLImpl::readPixelDataLowLevelTrack() {
	allocatePixelDataRowPointers();
	std::cout << "png_read_image" << std::endl;
	// libpng also supports readng line by line. But this is easiest
	png_read_image(pngSessionData, rowPointersLLT);
	std::cout << "png_read_end" << std::endl;
	png_read_end(pngSessionData, pngInfo);
}

void PngReaderLLImpl::checkPngMagicHeader(const unsigned char* buffer) {
	if (png_sig_cmp(buffer, 0, PNG_MAGIC_HEADER_SIZE)) {
		throw PngException("PNG does not contain the required PNG header");
	}
}

void PngReaderLLImpl::allocatePixelDataRowPointers() {
	deletePixelDataRowPointers();
	// Manual suggest to use png_malloc or allocate a big block and
	// set row_pointer[i] to proper place in the block.
	// With png_malloc
	rowPointersLLT = new png_bytep[height];
	for (int i = 0; i < height; i++) {
		rowPointersLLT[i] = NULL;
	}
	if (bitDepth == 16)
		nrOfBytesPerRow = width * 8; // RGBA, 2 bytes per channel
	else
		nrOfBytesPerRow = width * 4; // RGBA
	for (int i = 0; i < height; i++) {
		rowPointersLLT[i] = new png_byte[nrOfBytesPerRow];
	}
}

void PngReaderLLImpl::deletePixelDataRowPointers() {
	if (rowPointersLLT) {
		for (int i = 0; i < height; i++) {
			if (rowPointersLLT[i]) {
				delete[] rowPointersLLT[i];
			}
		}
		delete[] rowPointersLLT;
		rowPointersLLT = 0;
	}
}

void PngReaderLLImpl::deletePngLibResources() {
	// Free the memory associated with the pngSessionData and pngInfo.
	// See section 3.1 of
	// http://www.libpng.org/pub/png/libpng-1.4.0-manual.pdf
	// If High Level track is used, then rowPointerss are allocated by
	// libpng and thus also released by libpng.
	if (pngSessionData != NULL && pngInfo != NULL) {
		png_destroy_read_struct(&pngSessionData, &pngInfo, (png_infopp) NULL);
	} else if (pngSessionData != NULL) {
		png_destroy_read_struct(&pngSessionData, (png_infopp) NULL,
				(png_infopp) NULL);
	}
	pngSessionData = NULL;
	pngInfo = NULL;
}

void PngReaderLLImpl::setErrorHandlingWithSetjmp() {
	  if (setjmp(png_jmpbuf(pngSessionData))){
		// RAII is applied to release resources.
		// http://en.wikipedia.org/wiki/Resource_Acquisition_Is_Initialization
		throw PngException("Call to setjmp failed");
	}
}
void PngReaderLLImpl::printImageProperties() {
	// http://www.w3.org/TR/PNG-Chunks.html
	// color_type = 2 => each pixel is RGB triplet
	// if color_type=2, bit-depth = 8/16
	png_uint_32 colorType_ = colorType;
	png_uint_32 bitDepth_ = bitDepth;
	std::cout << "Image information width " << width << " height " << height
			<< " colorType " << colorType_ << " bitDepth " << bitDepth_
			<< " filterType " << filterType << " compressionType "
			<< compresssionType << " interlaceType " << interlaceType
			<< std::endl;
}
