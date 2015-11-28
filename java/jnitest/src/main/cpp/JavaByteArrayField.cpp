#include "JavaByteArrayField.h"
#include "jByteArrayPtrOut.h"
#include "JniAssert.h"

JavaByteArrayField::JavaByteArrayField(JNIEnv *env_, jclass javaClass, const char *fieldName) 
{
    env = env_;
    field = env->GetFieldID(javaClass, fieldName, "[B");
	JNI_ASSERT_NOT_NULL(field,"JavaByteArrayField::JavaByteArrayField, field is null"); 	
	javaObject = NULL;
  }
  
JavaByteArrayField::JavaByteArrayField(JNIEnv *env_, JavaByteArrayField &other, jobject obj) 
{
    env = env_; // JNIEnv is thread specific
	field = other.field;
	javaObject = obj;
  }
  
jbyteArray JavaByteArrayField::get()
  {
    jobject object = env->GetObjectField(javaObject, field);
    JNI_CHECK_EXCEPTION;
    jbyteArray byteArray = static_cast<jbyteArray>(object);  
    return byteArray;
  }

void JavaByteArrayField::set(const jbyte *bytes, const int nrOfBytes)
  {
     JByteArrayPtrOut jByteArrayPtrOut(env, bytes, nrOfBytes);
     env->SetObjectField(javaObject, field, jByteArrayPtrOut.getJByteArray());
     JNI_CHECK_EXCEPTION;
  }
