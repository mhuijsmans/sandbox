/*
 * PngWorker1.cpp
 *
 *  Created on: Aug 3, 2014
 *      Author: martien
 */

#include "IOUtils.h"
#include <fstream>      // std::ifstream
#include <iostream>     // std::cout

IOUtils::IOUtils() :
		buffer(0), length(0) {
}

IOUtils::~IOUtils() {
	deleteBuffer();
}

void IOUtils::readFile(const char *fileName) {
	deleteBuffer();

	std::ifstream file(fileName, std::ios::binary);
	file.seekg(0, std::ios::end);
	length = file.tellg();
	file.seekg(0, std::ios::beg);
	buffer = new unsigned char[length];

	std::cout << "Reading " << length << " characters... " << std::endl;
	// read data as a block:
	file.read((char *)buffer, length);

	if (file)
		std::cout << "all characters read successfully." << std::endl;
	else
		std::cout << "error: only " << file.gcount() << " could be read";
	file.close();
}

void IOUtils::deleteBuffer() {
	if (buffer) {
		delete[] buffer;
		buffer = 0;
		length = 0;
	}
}

