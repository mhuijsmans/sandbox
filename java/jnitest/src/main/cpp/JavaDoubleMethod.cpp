#include <string>
#include "JniAssert.h"
#include "JavaDoubleMethod.h"

  JavaDoubleMethod::JavaDoubleMethod(JNIEnv *env_, jclass javaClass, const char *methodName) {
    env = env_;
	std::string getMethodName("get");
	getMethodName.append(methodName);
    getMethod = env->GetMethodID(javaClass, getMethodName.c_str(), "()D");
	JNI_ASSERT_NOT_NULL(getMethod,"JavaDoubleMethod::JavaDoubleMethod, get method is null"); 
    //	
	std::string setMethodName("set");
	setMethodName.append(methodName);
    setMethod = env->GetMethodID(javaClass, setMethodName.c_str(), "(D)V");	
	JNI_ASSERT_NOT_NULL(setMethod,"JavaDoubleMethod::JavaDoubleMethod, set method is null"); 	
	javaObject = NULL;
  }
  
  JavaDoubleMethod::JavaDoubleMethod(JNIEnv *env_, JavaDoubleMethod &other, jobject obj) {
    env = env_; // JNIEnv is thread specific
	getMethod = other.getMethod;
	setMethod = other.setMethod;
	javaObject = obj;
  }
  
  jdouble JavaDoubleMethod::get()
  {
    jdouble value = env->CallDoubleMethod(javaObject, getMethod);
	JNI_CHECK_EXCEPTION;
	return value;
  }
  
  void JavaDoubleMethod::set(jdouble value)
  {
     env->CallVoidMethod(javaObject, setMethod, value);
	 JNI_CHECK_EXCEPTION;
  }
