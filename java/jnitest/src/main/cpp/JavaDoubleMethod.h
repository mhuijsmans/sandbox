#ifndef JavaDoubleMethod_H_
#define JavaDoubleMethod_H_

#include <jni.h>

class JavaDoubleMethod
{
public:
  JavaDoubleMethod(JNIEnv *env_, jclass javaClass, const char *methodName);
  JavaDoubleMethod(JNIEnv *env_, JavaDoubleMethod &other, jobject obj);
  jdouble get();
  void set(jdouble value);
private:
  JNIEnv *env;
  jmethodID getMethod;
  jmethodID setMethod;
  jobject javaObject;
};

#endif