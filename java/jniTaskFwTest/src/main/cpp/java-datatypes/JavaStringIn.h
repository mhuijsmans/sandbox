#ifndef JavaStringIn_H_
#define JavaStringIn_H_

#include <jni.h>

// Class that shall be used to access a received Java String.
class JavaStringIn {
public:
  JavaStringIn(JNIEnv *env, jstring inJNIStr);
  // Don't use the copy constructor
  JavaStringIn(const JavaStringIn &rhs);
  // Don't use the = operator
  JavaStringIn & operator=(const JavaStringIn &rhs);
  virtual ~JavaStringIn();
	
  bool isNull();
  const char * getString();  
  jsize getLength();

private:
  // memory allocated by JVM of which only inCStr has to be released
  JNIEnv *env;
  jstring inJNIStr;
  const char * inCStr;
  jsize lengthOfString;
};

#endif /* JavaStringIn_H_ */