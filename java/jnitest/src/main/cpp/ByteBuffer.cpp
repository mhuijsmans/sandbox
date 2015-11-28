#include <stdexcept>
#include "ByteBuffer.h"

ByteBuffer::ByteBuffer(const int length_)
{
	length = length_;
	if (length >0) {
		buffer = new char[length];
	} else {
		buffer = 0;
		length = -1;
	}
} 
   
ByteBuffer::~ByteBuffer()
{
  if (length>0) {
	delete [] buffer;
  }
}

// Don't use the copy constructor
ByteBuffer::ByteBuffer(const ByteBuffer &rhs)
{
  throw std::runtime_error("Don't use this constructor");
}

// Don't use the = operator
ByteBuffer & ByteBuffer::operator=(const ByteBuffer &rhs)
{
  throw std::runtime_error("Don't use this operator");
}