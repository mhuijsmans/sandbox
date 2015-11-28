#ifndef JavaBooleanField_H_
#define JavaBooleanField_H_

#include <jni.h>

class JavaBooleanField
{
public:
  JavaBooleanField(JNIEnv *env_, jclass javaClass, const char *fieldName);
  JavaBooleanField(JNIEnv *env_, JavaBooleanField &other, jobject obj);
  jboolean get();
  void set(jboolean value);
private:
  JNIEnv *env;
  jfieldID field;
  jobject javaObject;
};

#endif