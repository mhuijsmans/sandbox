#include "JavaDoubleField.h"
#include "JniAssert.h"

JavaDoubleField::JavaDoubleField(JNIEnv *env_, jclass javaClass, const char *fieldName) 
{
    env = env_;
    field = env->GetFieldID(javaClass, fieldName, "D");
	JNI_ASSERT_NOT_NULL(field,"JavaDoubleField::JavaDoubleField, field is null"); 	
	javaObject = NULL;
  }
  
JavaDoubleField::JavaDoubleField(JNIEnv *env_, JavaDoubleField &other, jobject obj) 
{
    env = env_; // JNIEnv is thread specific
	field = other.field;
	javaObject = obj;
  }
  
jdouble JavaDoubleField::get()
  {
    jdouble value = env->GetDoubleField(javaObject, field);
	JNI_CHECK_EXCEPTION;
	return value;
  }

void JavaDoubleField::set(jdouble value)
  {
     env->SetDoubleField(javaObject, field, value);
	 JNI_CHECK_EXCEPTION;
  }
