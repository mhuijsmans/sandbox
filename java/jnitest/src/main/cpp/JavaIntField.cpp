#include "JavaIntField.h"
#include "JniAssert.h"

JavaIntField::JavaIntField(JNIEnv *env_, jclass javaClass, const char *fieldName) 
{
    env = env_;
    field = env->GetFieldID(javaClass, fieldName, "I");
	JNI_ASSERT_NOT_NULL(field,"JavaIntField::JavaIntField, field is null"); 	
	javaObject = NULL;
  }
  
JavaIntField::JavaIntField(JNIEnv *env_, JavaIntField &other, jobject obj) 
{
    env = env_; // JNIEnv is thread specific
	field = other.field;
	javaObject = obj;
  }
  
jint JavaIntField::get()
  {
    jint value = env->GetIntField(javaObject, field);
	JNI_CHECK_EXCEPTION;
	return value;
  }

void JavaIntField::set(jint value)
  {
     env->SetIntField(javaObject, field, value);
	 JNI_CHECK_EXCEPTION;
  }
