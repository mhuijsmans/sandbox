#ifndef JavaIntMethod_H_
#define JavaIntMethod_H_

#include <jni.h>

class JavaIntMethod
{
public:
  JavaIntMethod(JNIEnv *env_, jclass javaClass, const char *methodName);
  JavaIntMethod(JNIEnv *env_, JavaIntMethod &other, jobject obj);
  jint get();
  void set(jint value);
private:
  JNIEnv *env;
  jmethodID getMethod;
  jmethodID setMethod;
  jobject javaObject;
};

#endif