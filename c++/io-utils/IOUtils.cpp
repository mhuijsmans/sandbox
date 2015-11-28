/*
 * Filename: IOUtils.cpp
 */

#include <cstdio>
#include <fstream> 
#include <iostream>
#include <sstream>
#include <stdexcept>
#include <sys/mman.h>

#include "IOUtils.h"

IOUtils::IOUtils() {
}

IOUtils::~IOUtils() {
}

// todo: memory mapped file: http://www.makelinux.net/alp/037

void IOUtils::CopyFile(const std::string &srcFileName,
		const std::string &destFileName) {
	std::ifstream src(srcFileName.c_str(), std::ios::in | std::ios::binary);
	if (!src.good()) {
		throw std::runtime_error(std::string("File not found: ") + srcFileName);
	}
	src.seekg(0, src.end);
	size_t length = src.tellg();
	src.seekg(0, src.beg);
	std::ofstream dest(destFileName.c_str(), std::ios::out | std::ios::binary);
	const int bufSize = 32 * 1024;
	char buf[bufSize];
	while (length > 0) {
		src.read(buf, bufSize);
		int bytesRead = src ? /* successful read*/bufSize : src.gcount();
		length -= bufSize;
		dest.write(buf, bytesRead);
	}
	src.close();
	dest.close();
}

std::string IOUtils::ReadFileToString(const std::string &fileName) {
	std::ifstream in;
	in.open(fileName); //open the input file
	if (!in.good()) {
		throw std::runtime_error(std::string("File not found: ") + fileName);
	}
	// ref: http://insanecoding.blogspot.nl/2011/11/how-to-read-in-file-in-c.html	
	return(std::string((std::istreambuf_iterator<char>(in)), std::istreambuf_iterator<char>()));
}

bool IOUtils::DeleteFile(const std::string &fileName) {
	// It is not clear from spec (http://www.cplusplus.com/reference/cstdio/remove/)
	// if remove will delete non-existing file
	std::ifstream src(fileName.c_str(), std::ios::in | std::ios::binary);
	if (src.good()) {
		return !remove(fileName.c_str());
	} else {
		return 1;
	}
}

