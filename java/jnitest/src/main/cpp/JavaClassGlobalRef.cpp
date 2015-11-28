#include <stdexcept>
#include <string>
#include "JavaClassGlobalRef.h"
#include "JavaStringIn.h"
#include "JniAssert.h"

#define JAVA_CLASS_DEBUG 1

JavaClassGlobalRef::JavaClassGlobalRef(JNIEnv *env_, const char *const className_) :
	className(className_) // class name will throw exception if null
   {
     env = env_;
	 // If class is not found, JVM will throws a java.lang.NoClassDefFoundError 
     javaClass = env->FindClass( className_);
	 JNI_ASSERT_NOT_NULL(javaClass,std::string("JavaClassGlobalRef::JavaClassGlobalRef, class not found: ")+className_);
	 javaClass = (jclass)env->NewGlobalRef(javaClass);
   } 
   
JavaClassGlobalRef::JavaClassGlobalRef(JNIEnv *env_, jclass javaClass_) :
	className("unknown")
{
   JNI_ASSERT_NOT_NULL(javaClass_,"JavaClassGlobalRef::JavaClassGlobalRef, jclass is null");
   env = env_;
   // No conversion to GlobalRef
   javaClass = javaClass_;
}

JavaClassGlobalRef::JavaClassGlobalRef(JNIEnv *env_, jobject javaObject_) :
	className("unknown")
{
   JNI_ASSERT_NOT_NULL(javaObject_,"JavaClassGlobalRef::JavaClassGlobalRef, jObject is null");
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
   
JavaClassGlobalRef::JavaClassGlobalRef(const JavaClassGlobalRef &rhs) :
   env(rhs.env), 
   javaClass(rhs.javaClass)
{
  throw std::runtime_error("Don't use this constructor");
}

JavaClassGlobalRef::JavaClassGlobalRef(JNIEnv *env_, JavaClassGlobalRef &javaCls) :
	env(env_),
	javaClass(javaCls.javaClass)
{
}

JavaClassGlobalRef::~JavaClassGlobalRef()
{
	// empty
}

JavaClassGlobalRef& JavaClassGlobalRef::operator=(const JavaClassGlobalRef &rhs)
{
  throw std::runtime_error("Don't use this operator");
}

jmethodID JavaClassGlobalRef::getMethodID( const char *name, const char *signature)
{
    JNI_ASSERT_NOT_NULL(name,"JavaClassGlobalRef::getMethodID, name is null");
	JNI_ASSERT_NOT_NULL(name,"JavaClassGlobalRef::getMethodID, signature is null");
	jmethodID method = env->GetMethodID( javaClass, name, signature);
	JNI_ASSERT_NOT_NULL(method,"JavaClassGlobalRef::getMethodID returned null");
	return method;
}

jfieldID JavaClassGlobalRef::getFieldID( const char *name, const char *signature)
{
	jfieldID field = env->GetFieldID(javaClass, name, signature);
    JNI_ASSERT_NOT_NULL(field,"JavaClassGlobalRef::getFieldID  returned null");	
	return field;
}

jobject JavaClassGlobalRef::newInstance() 
{
    jmethodID constructor = getMethodID("<init>", "()V");
	JNI_ASSERT_NOT_NULL(constructor,"JavaClassGlobalRef::newInstance, failed to retrieve default constructor");	
    jobject javaObject = env->NewObject(javaClass, constructor);
    JNI_ASSERT_NOT_NULL(javaObject,"JavaClassGlobalRef::newInstance, failed to create instance");	
    return javaObject;
}

jclass JavaClassGlobalRef::getClass() 
{
	return javaClass;
}
 