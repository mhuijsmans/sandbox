#ifndef JavaObjectMethod_H_
#define JavaObjectMethod_H_

#include <jni.h>

class JavaObjectMethod
{
public:
  JavaObjectMethod(JNIEnv *env_, jclass javaClass, const char *methodName, const char * signature);
  JavaObjectMethod(JNIEnv *env_, JavaObjectMethod &other, jobject obj);
  jobject get();
  void set(jobject value);
private:
  JNIEnv *env;
  jmethodID getMethod;
  jmethodID setMethod;
  jobject javaObject;
};

#endif