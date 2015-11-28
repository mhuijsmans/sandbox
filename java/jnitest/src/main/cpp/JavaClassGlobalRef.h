#ifndef JavaClassGlobalRef_H_
#define JavaClassGlobalRef_H_

#include <string>
#include <jni.h>

// Class that shall be used to hold global reference to Java class
class JavaClassGlobalRef {
public:
  // ONOY for this constructor is the resolved jclass references converted to global Reference
  JavaClassGlobalRef(JNIEnv *env, const char * const className);
  JavaClassGlobalRef(JNIEnv *env, jclass JavaClassGlobalRef);  
  JavaClassGlobalRef(JNIEnv *env, jobject javaObject);
  JavaClassGlobalRef(JNIEnv *env, JavaClassGlobalRef &javaCls);
  // Don't use the copy constructor
  JavaClassGlobalRef(const JavaClassGlobalRef &rhs);
  // Don't use the = operator
  JavaClassGlobalRef & operator=(const JavaClassGlobalRef &rhs);
  virtual ~JavaClassGlobalRef();
	
  jclass getClass(); 
  operator jclass  () {return javaClass;};

  jobject newInstance();  
  jmethodID getMethodID( const char *name, const char *signature);  
  jfieldID getFieldID( const char *name, const char *signature); 

private:
  // memory allocated by JVM of which only inCStr has to be released
  JNIEnv *env;
  std::string className;
  jclass javaClass;
};

#endif /* JavaClassGlobalRef_H_ */