#include <string>
#include "JniAssert.h"
#include "JavaStringMethod.h"

  JavaStringMethod::JavaStringMethod(JNIEnv *env_, jclass javaClass, const char *methodName) {
    env = env_;
	std::string getMethodName("get");
	getMethodName.append(methodName);
    getMethod = env->GetMethodID(javaClass, getMethodName.c_str(), "()Ljava/lang/String;");
	JNI_ASSERT_NOT_NULL(getMethod,"JavaStringMethod::JavaStringMethod, get method is null"); 
    //	
	std::string setMethodName("set");
	setMethodName.append(methodName);
    setMethod = env->GetMethodID(javaClass, setMethodName.c_str(), "(Ljava/lang/String;)V");	
	JNI_ASSERT_NOT_NULL(setMethod,"JavaStringMethod::JavaStringMethod, set method is null"); 	
	javaObject = NULL;
  }
  
  JavaStringMethod::JavaStringMethod(JNIEnv *env_, JavaStringMethod &other, jobject obj) {
    env = env_; // JNIEnv is thread specific
	getMethod = other.getMethod;
	setMethod = other.setMethod;
	javaObject = obj;
  }
  
  jstring JavaStringMethod::get()
  {
    jobject value = env->CallObjectMethod(javaObject, getMethod);
	JNI_CHECK_EXCEPTION;
    jstring castValue = static_cast<jstring>(value);  
    return castValue;
  }
  
  void JavaStringMethod::set(jstring value)
  {
     env->CallVoidMethod(javaObject, setMethod, value);
	 JNI_CHECK_EXCEPTION;
  }
