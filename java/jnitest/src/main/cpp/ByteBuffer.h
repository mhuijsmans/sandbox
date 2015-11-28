#ifndef ByteBuffer_H_
#define ByteBuffer_H_

class ByteBuffer {
public:
  ByteBuffer(const int length); 
  // Don't use the copy constructor
  ByteBuffer(const ByteBuffer &rhs);
  // Don't use the = operator
  ByteBuffer & operator=(const ByteBuffer &rhs);
  virtual ~ByteBuffer();
	
  char *getBuffer() {return buffer;};
  int getLength() {return length;};

private:
  // memory allocated by JVM of which only inCStr has to be released
  char *buffer;
  int length;
};

#endif /* ByteBuffer_H_ */