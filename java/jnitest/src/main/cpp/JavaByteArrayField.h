#ifndef JavaByteArrayField_H_
#define JavaByteArrayField_H_

#include <jni.h>

class JavaByteArrayField
{
public:
  JavaByteArrayField(JNIEnv *env_, jclass javaClass, const char *fieldName);
  JavaByteArrayField(JNIEnv *env_, JavaByteArrayField &other, jobject obj);
  jbyteArray get();
  void set(const jbyte *bytes, const int nrOfBytes);
private:
  JNIEnv *env;
  jfieldID field;
  jobject javaObject;
};

#endif