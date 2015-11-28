#ifndef JavaClass_H_
#define JavaClass_H_

#include <string>
#include <jni.h>

// Class that shall hold local reference to Java Class.
class JavaClass {
public:
  JavaClass(JNIEnv *env, const char * const className);
  JavaClass(JNIEnv *env, jclass javaClass);  
  JavaClass(JNIEnv *env, jobject javaObject);
  JavaClass(JNIEnv *env, JavaClass &javaCls);
  // Don't use the copy constructor
  JavaClass(const JavaClass &rhs);
  // Don't use the = operator
  JavaClass & operator=(const JavaClass &rhs);
  virtual ~JavaClass();
	
  jclass getClass(); 
  operator jclass  () {return javaClass;};

  jobject newInstance();  
  jmethodID getMethodID( const char *name, const char *signature);  
  jfieldID getFieldID( const char *name, const char *signature);
  std::string& getName() { return className; };

private:
  // memory allocated by JVM of which only inCStr has to be released
  JNIEnv *env;
  std::string className;
  jclass javaClass;
};

#endif /* JavaClass_H_ */
