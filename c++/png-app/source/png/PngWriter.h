/*
 * PngWriter.h
 *
 *  Created on: Aug 24, 2014
 *      Author: martien
 */

#ifndef PNGWRITER_H_
#define PNGWRITER_H_

#include "PngData.h"
#include "PngOutputStream.h"

class PngWriterImpl;

class PngWriter {
public:
	PngWriter(const PngData &png);
	virtual ~PngWriter();

	void writePngToOutputStream(PngOutputStream &pngOutputStream);
private:
	const PngData &png;
	PngWriterImpl *writer;
};

#endif /* PNGWRITER_H_ */
