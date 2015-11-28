#include <string>
#include "JniAssert.h"
#include "JavaObjectMethod.h"

  JavaObjectMethod::JavaObjectMethod(JNIEnv *env_, jclass javaClass, 
			const char *methodName, const char *methodClassName) {
    env = env_;
	std::string getMethodName("get");
	getMethodName.append(methodName);
	std::string getSignature("()L");
	getSignature.append(methodClassName);
	getSignature.append(";");
    getMethod = env->GetMethodID(javaClass, getMethodName.c_str(), getSignature.c_str());
	JNI_ASSERT_NOT_NULL(getMethod,"JavaObjectMethod::JavaObjectMethod, get method is null"); 
    //	
	std::string setMethodName("set");
	setMethodName.append(methodName);
	std::string setSignature("(L");
	setSignature.append(methodClassName);
	setSignature.append(";)V");	
    setMethod = env->GetMethodID(javaClass, setMethodName.c_str(), setSignature.c_str());	
	JNI_ASSERT_NOT_NULL(setMethod,"JavaObjectMethod::JavaObjectMethod, set method is null"); 	
	javaObject = NULL;
  }
  
  JavaObjectMethod::JavaObjectMethod(JNIEnv *env_, JavaObjectMethod &other, jobject obj) {
    env = env_; // JNIEnv is thread specific
	getMethod = other.getMethod;
	setMethod = other.setMethod;
	javaObject = obj;
  }
  
  jobject JavaObjectMethod::get()
  {
    jobject value = env->CallObjectMethod(javaObject, getMethod);
	JNI_CHECK_EXCEPTION;
    return value;
  }
  
  void JavaObjectMethod::set(jobject value)
  {
     env->CallVoidMethod(javaObject, setMethod, value);
	 JNI_CHECK_EXCEPTION;
  }
