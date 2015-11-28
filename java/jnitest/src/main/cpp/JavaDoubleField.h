#ifndef JavaDoubleField_H_
#define JavaDoubleField_H_

#include <jni.h>

class JavaDoubleField
{
public:
  JavaDoubleField(JNIEnv *env_, jclass javaClass, const char *fieldName);
  JavaDoubleField(JNIEnv *env_, JavaDoubleField &other, jobject obj);
  jdouble get();
  void set(jdouble value);
private:
  JNIEnv *env;
  jfieldID field;
  jobject javaObject;
};

#endif