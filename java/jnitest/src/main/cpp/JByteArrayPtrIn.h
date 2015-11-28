#ifndef JByteArrayPtrIn_H_
#define JByteArrayPtrIn_H_

#include <jni.h>

// Class that shall be used to access jByteArray bytes.
class JByteArrayPtrIn {
public:
  JByteArrayPtrIn(JNIEnv *env, jbyteArray bytes);
  // Don't use the copy constructor
  JByteArrayPtrIn(const JByteArrayPtrIn &rhs);
  // Don't use the = operator
  JByteArrayPtrIn & operator=(const JByteArrayPtrIn &rhs);
  virtual ~JByteArrayPtrIn();
	
  jbyte* getBytePtr();  
  jsize getNumberOfBytes();

private:
  // memory allocated by JVM of which only bufferPtr has to be released
  JNIEnv *env;
  jbyteArray bytes;
  jbyte* bufferPtr;
  jsize lengthOfArray;
};

#endif /* JByteArrayPtrIn_H_ */