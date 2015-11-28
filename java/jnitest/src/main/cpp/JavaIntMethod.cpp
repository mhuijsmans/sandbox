#include <string>
#include "JniAssert.h"
#include "JavaIntMethod.h"

  JavaIntMethod::JavaIntMethod(JNIEnv *env_, jclass javaClass, const char *methodName) {
    env = env_;
	std::string getMethodName("get");
	getMethodName.append(methodName);
    getMethod = env->GetMethodID(javaClass, getMethodName.c_str(), "()I");
	JNI_ASSERT_NOT_NULL(getMethod,"JavaIntMethod::JavaIntMethod, get method is null"); 
    //	
	std::string setMethodName("set");
	setMethodName.append(methodName);
    setMethod = env->GetMethodID(javaClass, setMethodName.c_str(), "(I)V");	
	JNI_ASSERT_NOT_NULL(setMethod,"JavaIntMethod::JavaIntMethod, set method is null"); 	
	javaObject = NULL;
  }
  
  JavaIntMethod::JavaIntMethod(JNIEnv *env_, JavaIntMethod &other, jobject obj) {
    env = env_; // JNIEnv is thread specific
	getMethod = other.getMethod;
	setMethod = other.setMethod;
	javaObject = obj;
  }
  
  jint JavaIntMethod::get()
  {
    jint value = env->CallIntMethod(javaObject, getMethod);
	JNI_CHECK_EXCEPTION;
	return value;
  }
  
  void JavaIntMethod::set(jint value)
  {
     env->CallVoidMethod(javaObject, setMethod, value);
	 JNI_CHECK_EXCEPTION;
  }
