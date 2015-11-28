#include "PngException.h"

PngException::PngException(const char* message) :
		msg(message) {
}

PngException::PngException(const std::string& message) :
		msg(message) {
}

PngException::~PngException() throw () {
}

