/*
 * PngWorker1.h
 *
 *  Created on: Aug 3, 2014
 *      Author: martien
 */

#ifndef PNGWORKER1_H_
#define PNGWORKER1_H_

#include <ios>

class IOUtils {
public:
	IOUtils();
	virtual ~IOUtils();

	// Read the file as binary data into a buffer
	void readFile(const char *fileName);

	const unsigned char * getBuffer() { return buffer; }
	std::streamsize getLength() { return length; }
private:
	void deleteBuffer();
	unsigned char * buffer;
	std::streamsize length;
};

#endif /* PNGWORKER1_H_ */
