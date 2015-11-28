#ifndef JavaStringOut_H_
#define JavaStringOut_H_

#include <jni.h>

// Class that shall be used to create a Java String to 
// be transferred to JVM
class JavaStringOut {
public:
  JavaStringOut(JNIEnv *env, const char *outCStr);
  // Don't use the copy constructor
  JavaStringOut(const JavaStringOut &rhs);
  // Don't use the = operator
  JavaStringOut & operator=(const JavaStringOut &rhs);
  virtual ~JavaStringOut();
	
  jstring getString(); 

  operator jstring  () {return javaString;};  

private:
  // memory allocated by JVM of which only inCStr has to be released
  JNIEnv *env;
  jstring javaString;
};

#endif /* JavaStringOut_H_ */