#include <stdexcept>
#include "JavaObject.h"
#include "JavaStringOut.h"
#include "JByteArrayPtrOut.h"
#include "JniAssert.h"

JavaObject::JavaObject(JNIEnv *env_, jclass javaclass_) :
	javaClass(env_,javaclass_)
{
    env = env_;
    createNewInstanceJObject();	
}

JavaObject::JavaObject(JNIEnv *env_, const char * className_) :
   javaClass(env_,className_)
{
    env = env_;
    createNewInstanceJObject();		
}

JavaObject::JavaObject(JNIEnv *env_, jclass javaclass_, jobject javaObject_) :
	javaClass(env_,javaclass_)
{
    JNI_ASSERT_NOT_NULL(javaObject_,"JavaObject::JavaObject, jObject is null");
    env = env_;
	javaObject = javaObject_;
	checkMatchObjectAndClass();
}

JavaObject::JavaObject(JNIEnv *env_, const char * className_, jobject javaObject_) :
   javaClass(env_,className_)
{
    JNI_ASSERT_NOT_NULL(javaObject_,"JavaObject::JavaObject, jObject is null");
    env = env_;
	javaObject = javaObject_;
	checkMatchObjectAndClass();
}

JavaObject::JavaObject(const JavaObject &rhs) :
	env(rhs.env), javaClass(rhs.javaClass), javaObject(rhs.javaObject)
{
  JNI_ASSERT("JavaObject::JavaObject, don't use this constructor");
}

JavaObject::~JavaObject()
{
	// empty
}

JavaObject& JavaObject::operator=(const JavaObject &rhs)
{
   JNI_ASSERT("JavaObject::JavaObject, don't use this operator");
}

jobject JavaObject::getObject(const char *methodName, const char *signature)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getObject, methodName is null");
  JNI_ASSERT_NOT_NULL(signature,"JavaObject::getObject, signature is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, signature);
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getObject, method not found");
  jobject object = env->CallObjectMethod (javaObject, method);
  JNI_CHECK_EXCEPTION;
  return object;
}

jstring JavaObject::getString(const char *methodName) 
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getString, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "()Ljava/lang/String;");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getString, method not found");
  jobject object = env->CallObjectMethod (javaObject, method);
  JNI_CHECK_EXCEPTION;
  jstring string = static_cast<jstring>(object);  
  return string;
}

void JavaObject::setString(const char* methodName, const char *value) 
{
    JNI_ASSERT_NOT_NULL(methodName,"JavaObject::setString, methodName is null");
    jmethodID method = javaClass.getMethodID(methodName, "(Ljava/lang/String;)V");
	JNI_ASSERT_NOT_NULL(method,"JavaObject::setString, method is null");
	JavaStringOut javastringOut(env, value);
	env->CallVoidMethod(javaObject, method, javastringOut.getString());
	JNI_CHECK_EXCEPTION;
}

void JavaObject::setString(const char* methodName, const std::string &string)
{
  setString(methodName,string.c_str()); 
}

jboolean JavaObject::getBoolean(const char *methodName)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getBoolean, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "()Z");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getBoolean, method is null");
  jboolean val = env->CallBooleanMethod(javaObject, method);
  JNI_CHECK_EXCEPTION;
  return val;
}

void JavaObject::setBoolean(const char *methodName, const jboolean value)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::setBoolean, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "(Z)V");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::setBoolean, method is null");  
  env->CallVoidMethod(javaObject, method, value);
  JNI_CHECK_EXCEPTION;
}

jint JavaObject::getInt(const char *methodName) 
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getInt, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "()I");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getInt, method is null");  
  jint val = env->CallIntMethod(javaObject, method);
  JNI_CHECK_EXCEPTION;
  return val;
}

void JavaObject::setInt(const char *methodName, const jint value) 
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::setInt, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "(I)V");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::setInt, method is null");    
  env->CallVoidMethod(javaObject, method, value);
  JNI_CHECK_EXCEPTION;
}

jfloat JavaObject::getFloat(const char *methodName)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getFloat, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "()F");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getFloat, method is null");    
  jfloat val = env->CallFloatMethod(javaObject, method);
  JNI_CHECK_EXCEPTION;
  return val;
}
void JavaObject::setFloat(const char *methodName, const jfloat value)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::setFloat, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "(F)V");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::setFloat, method is null");      
  env->CallVoidMethod(javaObject, method, value);
  JNI_CHECK_EXCEPTION;
}

jdouble JavaObject::getDouble(const char *methodName)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getDouble, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "()D");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getDouble, method is null");    
  jdouble val = env->CallDoubleMethod(javaObject, method);
  JNI_CHECK_EXCEPTION;
  return val;
}

void JavaObject::setDouble(const char *methodName, const jdouble value)
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::setDouble, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "(D)V");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::setDouble, method is null");      
  env->CallVoidMethod(javaObject, method, value);
  JNI_CHECK_EXCEPTION;
}

jbyteArray JavaObject::getBytes(const char *methodName) 
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::getBytes, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "()[B");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::getBytes, method is null");        
  jobject object = env->CallObjectMethod (javaObject, method);
  jbyteArray byteArray = static_cast<jbyteArray>(object); 
  JNI_CHECK_EXCEPTION;  
  return byteArray;
}

void JavaObject::setBytes(const char *methodName, const jbyte *bytes, const int nrOfBytes) 
{
  JNI_ASSERT_NOT_NULL(methodName,"JavaObject::setBytes, methodName is null");
  jmethodID method = env->GetMethodID(javaClass, methodName, "([B)V");
  JNI_ASSERT_NOT_NULL(method,"JavaObject::setBytes, method is null");          
  JByteArrayPtrOut jByteArrayPtrOut(env, bytes, nrOfBytes);
  env->CallVoidMethod(javaObject, method, jByteArrayPtrOut.getJByteArray());
  JNI_CHECK_EXCEPTION;
}

jobject JavaObject::getObjectField(const char *fieldName, const char *signature)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getObjectField, fieldName is null");
  JNI_ASSERT_NOT_NULL(signature,"JavaObject::getObjectField, signature is null");  
  jfieldID field = env->GetFieldID(javaClass, fieldName, signature); 
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getObjectField, field is null");          
  jobject object = env->GetObjectField(javaObject, field);  
  JNI_CHECK_EXCEPTION;
  return object;
} 

jstring JavaObject::getStringField(const char *fieldName)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getStringField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "Ljava/lang/String;"); 
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getStringField, field is null");          
  jobject object = env->GetObjectField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  jstring string = static_cast<jstring>(object);  
  return string;
}

void JavaObject::setStringField(const char* fieldName, const char *value)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setStringField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "Ljava/lang/String;");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setStringField, field is null");          
  JavaStringOut javastringOut(env, value);
  env->SetObjectField(javaObject, field, javastringOut.getString());
  JNI_CHECK_EXCEPTION;
}

void JavaObject::setStringField(const char* fieldName, const std::string &string)
{
  setStringField(fieldName,string.c_str()); 
}
  
jboolean JavaObject::getBooleanField(const char *fieldName) 
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getBooleanField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "Z");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getBooleanField, field is null");          
  jboolean value = env->GetBooleanField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  return value;
}

void JavaObject::setBooleanField(const char *fieldName, const jboolean value)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setBooleanField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "Z");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setBooleanField, field is null");          
  env->SetBooleanField(javaObject, field, value);
  JNI_CHECK_EXCEPTION;
}

jint JavaObject::getIntField(const char *fieldName)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getIntField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "I");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getIntField, field is null");          
  jint value = env->GetIntField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  return value;
}

void JavaObject::setIntField(const char *fieldName, const jint value)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setIntField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "I");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setIntField, field is null");            
  env->SetIntField(javaObject, field, value);
  JNI_CHECK_EXCEPTION;
}

jlong JavaObject::getLongField(const char *fieldName)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getLomgField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "J");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getLongField, field is null");          
  jlong value = env->GetLongField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  return value;
}

void JavaObject::setLongField(const char *fieldName, const jlong value)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setLongField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "J");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setLongField, field is null");            
  env->SetLongField(javaObject, field, value);
  JNI_CHECK_EXCEPTION;
}

jfloat JavaObject::getFloatField(const char *fieldName)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getFloatField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "F");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getFloatField, field is null");              
  jfloat value = env->GetFloatField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  return value;
}

void JavaObject::setFloatField(const char *fieldName, const jfloat value)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setFloatField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "F");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setFloatField, field is null");                
  env->SetFloatField(javaObject, field, value);
  JNI_CHECK_EXCEPTION;
} 

jdouble JavaObject::getDoubleField(const char *fieldName)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getDoubleField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "D");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getDoubleField, field is null");              
  jdouble value = env->GetDoubleField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  return value;
}

void JavaObject::setDoubleField(const char *fieldName, const jdouble value)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setDoubleField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "D");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setDoubleField, field is null");                
  env->SetDoubleField(javaObject, field, value);
  JNI_CHECK_EXCEPTION;
} 

void JavaObject::setBytesField(const char *fieldName, const jbyte *bytes, const int nrOfBytes)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::setFloatField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "[B");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::setBytesField, field is null");                  
  JByteArrayPtrOut jByteArrayPtrOut(env, bytes, nrOfBytes);
  env->SetObjectField(javaObject, field, jByteArrayPtrOut.getJByteArray());
  JNI_CHECK_EXCEPTION;
}

jbyteArray JavaObject::getBytesField(const char *fieldName)
{
  JNI_ASSERT_NOT_NULL(fieldName,"JavaObject::getBytesField, fieldName is null");
  jfieldID field = env->GetFieldID(javaClass, fieldName, "[B");
  JNI_ASSERT_NOT_NULL(field,"JavaObject::getBytesField, field is null");                    
  jobject object = env->GetObjectField(javaObject, field);
  JNI_CHECK_EXCEPTION;
  jbyteArray byteArray = static_cast<jbyteArray>(object);  
  return byteArray;
}

void JavaObject::checkMatchObjectAndClass()
{
  if (!(env->IsInstanceOf(javaObject,javaClass.getClass()))) { 
	throw std::runtime_error("Mismatch object and class");
  }
  JNI_CHECK_EXCEPTION;
}
   
void JavaObject::createNewInstanceJObject() 
{
    jmethodID constructor = env->GetMethodID(javaClass.getClass(), "<init>", "()V");
    JNI_ASSERT_NOT_NULL(constructor,"JavaObject::createNewInstanceJObject, constructor is null"); 	
	javaObject = env->NewObject(javaClass.getClass(), constructor);
    JNI_ASSERT_NOT_NULL(javaObject,"JavaObject::createNewInstanceJObject, javaObject is null"); 	
}   
