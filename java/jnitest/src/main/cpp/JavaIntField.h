#ifndef JavaIntField_H_
#define JavaIntField_H_

#include <jni.h>

class JavaIntField
{
public:
  JavaIntField(JNIEnv *env_, jclass javaClass, const char *fieldName);
  JavaIntField(JNIEnv *env_, JavaIntField &other, jobject obj);
  jint get();
  void set(jint value);
private:
  JNIEnv *env;
  jfieldID field;
  jobject javaObject;
};

#endif