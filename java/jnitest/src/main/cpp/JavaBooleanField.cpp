#include "JavaBooleanField.h"
#include "JniAssert.h"

JavaBooleanField::JavaBooleanField(JNIEnv *env_, jclass javaClass, const char *fieldName) 
{
    env = env_;
    field = env->GetFieldID(javaClass, fieldName, "Z");
	JNI_ASSERT_NOT_NULL(field,"JavaBooleanField::JavaBooleanField, field is null"); 	
	javaObject = NULL;
  }
  
JavaBooleanField::JavaBooleanField(JNIEnv *env_, JavaBooleanField &other, jobject obj) 
{
    env = env_; // JNIEnv is thread specific
	field = other.field;
	javaObject = obj;
  }
  
jboolean JavaBooleanField::get()
  {
    jboolean value = env->GetBooleanField(javaObject, field);
	JNI_CHECK_EXCEPTION;
	return value;
  }

void JavaBooleanField::set(jboolean value)
  {
     env->SetBooleanField(javaObject, field, value);
	 JNI_CHECK_EXCEPTION;
  }
