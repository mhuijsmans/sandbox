/*
 * PngWriter.cpp
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#include <libpng16/png.h>
#include <libpng16/pngconf.h>
#include <csetjmp>
#include <cstdio>
#include <stdexcept>

#include "PngWriter.h"
#include "PngException.h"

class PngWriterImpl {
public:
	PngWriterImpl(const PngData &png, PngOutputStream &pngOutputStream);
	virtual ~PngWriterImpl();

	void writePngToOutputStream();

private:
	void writeHeader();
	void writePixelData();
	void endWrite();
	void setCompressionLevel();
	void createWriteStruct();
	void createInfoStruct();
	void setErrorHandlingWithSetjmp();

	void writeDataToOutputstream(png_bytep outBytesPtr,
			png_size_t nrOfBytesToRead);
	static void writeDataToOutputstreamProxy(png_structp png_ptr,
			png_bytep data, png_size_t length);

	void flushOutputstream();
	static void flushOutputstreamProxy(png_structp png_ptr);

	void freeResources();
	void informLibPngAboutOutputSource();

	png_structp pngSessionData;
	png_infop pngInfo;
	const PngData &png;

	PngOutputStream &pngOutputStream;
};

PngWriter::PngWriter(const PngData & png_) :
		png(png_), writer(0) {
}

PngWriter::~PngWriter() {
	if (writer) {
		delete writer;
	}
}

void PngWriter::writePngToOutputStream(PngOutputStream &pngOutputStream) {
	if (writer) {
		throw std::runtime_error("Method can be called only once");
	}
	writer = new PngWriterImpl(png, pngOutputStream);
	writer->writePngToOutputStream();
}

// #############################################################

PngWriterImpl::PngWriterImpl(const PngData &png_,
		PngOutputStream &pngOutputStream_) :
		pngSessionData(0), pngInfo(0), png(png_), pngOutputStream(
				pngOutputStream_) {
}

PngWriterImpl::~PngWriterImpl() {
	freeResources();
}

void PngWriterImpl::freeResources() {
	if (pngSessionData != NULL && pngInfo != NULL) {
		png_destroy_write_struct(&pngSessionData, &pngInfo);
	} else if (pngSessionData != NULL) {
		png_destroy_write_struct(&pngSessionData, NULL);
	}
	pngSessionData = NULL;
	pngInfo = NULL;
}

void PngWriterImpl::writePngToOutputStream() {
	createWriteStruct();
	createInfoStruct();
	setErrorHandlingWithSetjmp();
	setCompressionLevel();
	informLibPngAboutOutputSource();
	writeHeader();
	writePixelData();
	endWrite();
}

void PngWriterImpl::informLibPngAboutOutputSource() {
	png_voidp io_ptr = (png_voidp) ((this));
	png_rw_ptr writeDataFunctionPtr =
			static_cast<png_rw_ptr>(&PngWriterImpl::writeDataToOutputstreamProxy);
	png_flush_ptr flushDataFunctionPtr =
			static_cast<png_flush_ptr>(&PngWriterImpl::flushOutputstreamProxy);
	/* if my_png_flush() is not needed, change the arg to NULL */
	png_set_write_fn(pngSessionData, io_ptr, writeDataFunctionPtr,
			flushDataFunctionPtr);
}

void PngWriterImpl::writeDataToOutputstream(png_bytep outBytesPtr,
		png_size_t nrOfBytesToRead) {
	pngOutputStream.writeData(outBytesPtr, nrOfBytesToRead);
}

void PngWriterImpl::writeDataToOutputstreamProxy(png_structp png_ptr,
		png_bytep data, png_size_t length) {
	PngWriterImpl *pngWriterImpl = (PngWriterImpl *) png_get_io_ptr(png_ptr);
	if (pngWriterImpl == NULL) {
		throw PngException("Invalid input, png_get_io_ptr returned NULL");
	}
	// -> replace with your own data source interface
	pngWriterImpl->writeDataToOutputstream(data, length);
}

void PngWriterImpl::flushOutputstream() {
	pngOutputStream.flush();
}

void PngWriterImpl::flushOutputstreamProxy(png_structp png_ptr) {
	PngWriterImpl *pngWriterImpl = (PngWriterImpl *) png_get_io_ptr(png_ptr);
	if (pngWriterImpl == NULL) {
		throw PngException("Invalid input, png_get_io_ptr returned NULL");
	}
	// -> replace with your own data source interface
	pngWriterImpl->flushOutputstream();
}

void PngWriterImpl::setCompressionLevel() {
	// disable compression.
	// But this create files larger than just storing the raw bitmap.
	// ref Configuring zlib
	// at: http://www.libpng.org/pub/png/libpng-1.2.5-manual.html#section-4.5
	png_set_compression_level(pngSessionData, 0); // Z_NO_COMPRESSION
	// 2nd par must be same as in png_set_IHDR()
	// ref: http://www.libpng.org/pub/png/libpng-1.2.5-manual.html#section-4.5
	// Setting this filter has big impact
	png_set_filter(pngSessionData, PNG_FILTER_TYPE_BASE, PNG_FILTER_NONE);
	// I didn't observe significant performance changes for next function.
	// png_set_filter_heuristics(png_ptr,PNG_FILTER_HEURISTIC_DEFAULT,0,NULL,NULL);
}

void PngWriterImpl::writeHeader() {

	int interlace_type = PNG_INTERLACE_NONE;
	int compression_type = PNG_COMPRESSION_TYPE_BASE;
	int filter_type = PNG_FILTER_TYPE_BASE;
	png_set_IHDR(pngSessionData, pngInfo, png.width, png.height, png.bitDepth,
			png.colorType, interlace_type, compression_type, filter_type);
	png_write_info(pngSessionData, pngInfo);
}

void PngWriterImpl::writePixelData() {
	/* write bytes */
	png_write_image(pngSessionData, png.rowPointers);
}

void PngWriterImpl::endWrite() {
	/* end write */
	png_write_end(pngSessionData, NULL);
}

void PngWriterImpl::createWriteStruct() {
	pngSessionData = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL,
	NULL, NULL);
	if (!pngSessionData) {
		throw PngException("Failed to read png_structp");
	}
}

void PngWriterImpl::createInfoStruct() {
	pngInfo = png_create_info_struct(pngSessionData);
	if (!pngInfo) {
		throw PngException("Failed to read png_infop");
	}
}

void PngWriterImpl::setErrorHandlingWithSetjmp() {
	if (setjmp(png_jmpbuf(pngSessionData))) {
		// RAII is applied to release resources.
		// http://en.wikipedia.org/wiki/Resource_Acquisition_Is_Initialization
		throw PngException("Call to setjmp failed");
	}
}

