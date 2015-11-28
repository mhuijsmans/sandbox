#ifndef JByteArrayPtrOut_H_
#define JByteArrayPtrOut_H_

#include <jni.h>

// Class that shall be used to create and fill a jbyteArray 
// to transfer bytes from C++ -> Java
class JByteArrayPtrOut {
public:
  JByteArrayPtrOut(JNIEnv *env_, const jbyte * bytes, const int nrOfBytes);
  // Don't use the copy constructor
  JByteArrayPtrOut(const JByteArrayPtrOut &rhs);
  // Don't use the = operator
  JByteArrayPtrOut & operator=(const JByteArrayPtrOut &rhs);
  virtual ~JByteArrayPtrOut();
	
  jbyteArray getJByteArray();
  operator jbyteArray  () {return result;};  

private:
  JNIEnv *env;
  jbyteArray result;
};

#endif /* JByteArrayPtrOut_H_ */