#include <cstring>
#include <iostream>

#include "Chrono.h"

int main() {

	const int nrOfBytes = 25 * 1024 * 1024;

	char *ptr1 = new char[nrOfBytes];
	char *ptr2 = new char[nrOfBytes];
	Chrono chrono;
	int max = 750;
	for (int i = 0; i < max; i++) {
		memcpy(ptr1, ptr2, nrOfBytes);
	}
	long elapsedMS = chrono.elapsedTimeInMs();
	long nrOfMb = ((long) nrOfBytes * max) / (1024 * 1024);
	long nrOfGb = ((long) nrOfBytes * max) / (1024 * 1024 * 1024);
	std::cout << "Max: " << max << ", bufferSize(mb): "
			<< (nrOfBytes / (1024 * 1024)) << ", elapsed(ms): " << elapsedMS
			<< ", avg(ms): " << (elapsedMS / max) << ", amount of data (mb): "
			<< nrOfMb << ", amount of data (gb): " << nrOfGb << std::endl;

}
