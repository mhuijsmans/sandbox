#ifndef JavaObject_H_
#define JavaObject_H_

#include <string>
#include <jni.h>
#include "JavaClass.h"

// Class that shall be used to create a Java String to 
// be transferred to JVM
class JavaObject {
public:
  // Create instance of the provide class
  JavaObject(JNIEnv *env, jclass javaclass);
  JavaObject(JNIEnv *env, const char * className);
  JavaObject(JNIEnv *env, const char * className, jobject javaObject);
  JavaObject(JNIEnv *env, jclass javaclass, jobject javaObject);
  // Don't use the copy constructor
  JavaObject(const JavaObject &rhs);
  // Don't use the = operator
  JavaObject & operator=(const JavaObject &rhs);
  virtual ~JavaObject();
  
  jobject getObject(const char *methodName, const char *signature);
  
  jstring getString(const char *methodName);
  void setString(const char* methodName, const char *value);
  void setString(const char* methodName, const std::string &string);
  
  jboolean getBoolean(const char *methodName);
  void setBoolean(const char *methodName, const jboolean value);
  
  jint getInt(const char *methodName);
  void setInt(const char *methodName, const jint value);
  
  jfloat getFloat(const char *methodName);
  void setFloat(const char *methodName, const jfloat value);  
  
  jdouble getDouble(const char *methodName);
  void setDouble(const char *methodName, const jdouble value);   
  
  void setBytes(const char *methodName, const jbyte *bytes, const int nrOfBytes);
  jbyteArray getBytes(const char *methodName);  
  
  jobject getObjectField(const char *methodName, const char *signature);  
  
  jstring getStringField(const char *fieldName);
  void setStringField(const char* fieldName, const char *value);
  void setStringField(const char* fieldName, const std::string &string);
  
  jboolean getBooleanField(const char *fieldName);
  void setBooleanField(const char *fieldName, const jboolean value);  
  
  jint getIntField(const char *fieldName);
  void setIntField(const char *fieldName, const jint value);
  
  jlong getLongField(const char *fieldName);
  void setLongField(const char *fieldName, const jlong value);  
  
  jfloat getFloatField(const char *fieldName);
  void setFloatField(const char *fieldName, const jfloat value);  
  
  jdouble getDoubleField(const char *fieldName);
  void setDoubleField(const char *fieldName, const jdouble value);
  
  void setBytesField(const char *fieldName, const jbyte *bytes, const int nrOfBytes);
  jbyteArray getBytesField(const char *fieldName);  
  
  jclass getClass() {return javaClass;};
  operator jclass  () {return javaClass;};  
  
  jobject getJObject() { return javaObject; };
  operator jobject() {return javaObject;};  

private:
  // memory allocated by JVM of which only inCStr has to be released
  JNIEnv *env;
  JavaClass javaClass;
  jobject javaObject;
  
  void checkMatchObjectAndClass();
  void createNewInstanceJObject() ;
};

#endif /* JavaObject_H_ */