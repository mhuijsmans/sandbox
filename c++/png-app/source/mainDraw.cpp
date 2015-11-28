#include <stdexcept>
#include <iostream>

#define ASSERT_TRUE(a,b) { if (!(a)) throw std::runtime_error(b); }

#include "drawing/ImageIO.h"
#include "drawing/PixelData.h"
#include "util/PrintUtil.h"

int main(int argc, char **argv) {

	int width = 5;
	int height = 5;
	PixelData pixelData(width, height, 16);

	PrintUtil::printHex(pixelData.pixelData, pixelData.GetNrOfBytes(), width*6);

	BufferType bufferType = interleaved;
	ImageIO imageIo(pixelData.pixelData, pixelData.width, pixelData.height,
			bufferType);
	const int nrOfPixels = imageIo.GetNrOfPixels();

	//PrintUtil::printHex(pixelData.pixelData, pixelData.GetNrOfBytes());

	int nrOfmatchingPixels = imageIo.GetNrOfMatchingPixels(Colors::white);
	ASSERT_TRUE(nrOfPixels == nrOfmatchingPixels, "Not all pixels are white");

	imageIo.Fill(Colors::black);
	nrOfmatchingPixels = imageIo.GetNrOfMatchingPixels(Colors::black);
	ASSERT_TRUE(nrOfPixels == nrOfmatchingPixels, "Not all pixels are white");

	imageIo.Fill(Colors::white);
	imageIo.DrawLine(1, 1, 1, 1, Colors::red);
	nrOfmatchingPixels = imageIo.GetNrOfMatchingPixels(Colors::red);
	ASSERT_TRUE(1 == nrOfmatchingPixels, "Mismatch-1");
	imageIo.DrawRectangle(2, 2, 3, 3, Colors::green);
	nrOfmatchingPixels = imageIo.GetNrOfMatchingPixels(Colors::red);
	ASSERT_TRUE(1 == nrOfmatchingPixels, "Mismatch-2");
	nrOfmatchingPixels = imageIo.GetNrOfMatchingPixels(Colors::green);
	ASSERT_TRUE(8 == nrOfmatchingPixels, "Mismatch-3");

	PrintUtil::printHex(pixelData.pixelData, pixelData.GetNrOfBytes(), width*6);

	imageIo.Fill(Colors::white);
	imageIo.DrawFilledRectangle(1, 1, 3, 3, Colors::green);
	nrOfmatchingPixels = imageIo.GetNrOfMatchingPixels(Colors::green);
	ASSERT_TRUE(9 == nrOfmatchingPixels, "Mismatch-4");

	PrintUtil::printHex(pixelData.pixelData, pixelData.GetNrOfBytes(), width*6);

	std::cout << "All is ok" << std::endl;
	return 0;
}
