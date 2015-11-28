#include <stdexcept>
#include <string>
#include "JavaClass.h"
#include "JavaStringIn.h"
#include "JniAssert.h"

#define JAVA_CLASS_DEBUG 1

JavaClass::JavaClass(JNIEnv *env_, const char *const className_) :
	className(className_) // class name will throw exception if null
   {
     env = env_;
	 // If class is not found, JVM will throws a java.lang.NoClassDefFoundError 
     javaClass = env->FindClass( className_);
	 JNI_ASSERT_NOT_NULL(javaClass,std::string("JavaClass::JavaClass, class not found: ")+className_);
   } 
   
JavaClass::JavaClass(JNIEnv *env_, jclass javaClass_) :
	className("unknown")
{
   JNI_ASSERT_NOT_NULL(javaClass_,"JavaClass::JavaClass, jclass is null");
   env = env_;
   javaClass = javaClass_;
}

JavaClass::JavaClass(JNIEnv *env_, jobject javaObject_) :
	className("unknown")
{
   JNI_ASSERT_NOT_NULL(javaObject_,"JavaClass::JavaClass, jObject is null");
   env = env_;
   jclass objectClass = env->GetObjectClass( javaObject_ );
   JNI_CHECK_EXCEPTION;
   if (JAVA_CLASS_DEBUG) {
     // First get the class object
     jmethodID method = env->GetMethodID(objectClass, "getClass", "()Ljava/lang/Class;");
	 JNI_CHECK_EXCEPTION;
     jobject classOfObject = env->CallObjectMethod(javaObject_, method);
	 JNI_CHECK_EXCEPTION;
     // Now get the class object's class descriptor
     jclass theJavaClass = env->GetObjectClass(classOfObject);
	 JNI_CHECK_EXCEPTION;

     // Find the getName() method on the class object
     method = env->GetMethodID(theJavaClass, "getName", "()Ljava/lang/String;");
	 JNI_CHECK_EXCEPTION;

     // Call the getName() to get a jstring object back
     JavaStringIn javaObjectClassName(env, (jstring)env->CallObjectMethod(classOfObject, method));
     className.assign(javaObjectClassName.getString());   
   }
}
   
JavaClass::JavaClass(const JavaClass &rhs) :
   env(rhs.env), 
   javaClass(rhs.javaClass)
{
  throw std::runtime_error("Don't use this constructor");
}

JavaClass::JavaClass(JNIEnv *env_, JavaClass &javaCls) :
	env(env_),
	javaClass(javaCls.javaClass)
{
}

JavaClass::~JavaClass()
{
	// empty
}

JavaClass& JavaClass::operator=(const JavaClass &rhs)
{
  throw std::runtime_error("Don't use this operator");
}

jmethodID JavaClass::getMethodID( const char *name, const char *signature)
{
    JNI_ASSERT_NOT_NULL(name,"JavaClass::getMethodID, name is null");
	JNI_ASSERT_NOT_NULL(name,"JavaClass::getMethodID, signature is null");
	jmethodID method = env->GetMethodID( javaClass, name, signature);
	JNI_ASSERT_NOT_NULL(method,"JavaClass::getMethodID returned null");
	return method;
}

jfieldID JavaClass::getFieldID( const char *name, const char *signature)
{
	jfieldID field = env->GetFieldID(javaClass, name, signature);
    JNI_ASSERT_NOT_NULL(field,"JavaClass::getFieldID  returned null");	
	return field;
}

jobject JavaClass::newInstance() 
{
    jmethodID constructor = getMethodID("<init>", "()V");
	JNI_ASSERT_NOT_NULL(constructor,"JavaClass::newInstance, failed to retrieve default constructor");	
    jobject javaObject = env->NewObject(javaClass, constructor);
    JNI_ASSERT_NOT_NULL(javaObject,"JavaClass::newInstance, failed to create instance");	
    return javaObject;
}

jclass JavaClass::getClass() 
{
	return javaClass;
}
 