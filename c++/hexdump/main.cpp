#include <iostream>
#include "Utils.h"


int main()
{
	const static size_t size=100;
	char b[size];
	Utils::printHex(b, size);
}
