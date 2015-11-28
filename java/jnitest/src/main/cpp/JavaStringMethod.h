#ifndef JavaStringMethod_H_
#define JavaStringMethod_H_

#include <jni.h>

class JavaStringMethod
{
public:
  JavaStringMethod(JNIEnv *env_, jclass javaClass, const char *methodName);
  JavaStringMethod(JNIEnv *env_, JavaStringMethod &other, jobject obj);
  jstring get();
  void set(jstring value);
private:
  JNIEnv *env;
  jmethodID getMethod;
  jmethodID setMethod;
  jobject javaObject;
};

#endif