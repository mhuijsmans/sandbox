/*
 * FilePngInputStream.cpp
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#include "FilePngInputStream.h"

#include <fstream>      // std::ifstream

class FilePngImpl {
public:
	FilePngImpl(const char *fileName) :
			file(0) {
	file = new std::ifstream(fileName, std::ios::binary);
}
~FilePngImpl() {
	if (file) {
		delete file;
	}
}

void readData(png_bytep outBytesPtr, png_size_t nrOfBytesToRead) {
	file->read((char *) outBytesPtr, nrOfBytesToRead);
}
private:
std::ifstream * file;
};

FilePngInputStream::FilePngInputStream(const char *fileName) :
		file(0) {
	file = new FilePngImpl(fileName);
}

FilePngInputStream::~FilePngInputStream() {
	if (file) {
		delete file;
	}
}

void FilePngInputStream::readData(png_bytep outBytesPtr,
		png_size_t nrOfBytesToRead) {
	file->readData(outBytesPtr, nrOfBytesToRead);
}

