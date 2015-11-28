#include <jni.h>
#include <stdio.h>
#include <string.h>

#include <stdexcept>
#include <iostream>  // for std::cout

#include "org_mahu_proto_jnitest_nativewrapper_HelloJNI.h"
#include "JavaClass.h"
#include "JavaObject.h"
#include "JByteArrayPtrIn.h"
#include "JByteArrayPtrOut.h"
#include "JavaStringIn.h"
#include "JavaStringOut.h"
#include "ThrowJavaRuntimeException.h"


// ################################################

class FinallyClass
{
public:
    FinallyClass()
    {
         printf("FinallyClass.ctor()\n");
    }
    ~FinallyClass()
    {
        printf("FinallyClass.dtor()\n");
    }
};

// ################################################

class MyException: public std::exception
{
public:
    /** Constructor (C strings).
     *  @param message C-style string error message.
     *                 The string contents are copied upon construction.
     *                 Hence, responsibility for deleting the \c char* lies
     *                 with the caller. 
     */
    explicit MyException(const char* message):
      msg_(message)
      {
      }

    /** Constructor (C++ STL strings).
     *  @param message The error message.
     */
    explicit MyException(const std::string& message):
      msg_(message)
      {}

    /** Destructor.
     * Virtual to allow for subclassing.
     */
    virtual ~MyException() throw (){}

    /** Returns a pointer to the (constant) error description.
     *  @return A pointer to a \c const \c char*. The underlying memory
     *          is in posession of the \c Exception object. Callers \a must
     *          not attempt to free the memory.
     */
    virtual const char* what() const throw (){
       return msg_.c_str();
    }

protected:
    /** Error message.
     */
    std::string msg_;
};

// ################################################

jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase1_helper
  (JNIEnv *env, jobject obj, jint test);

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    testErrorCase1
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase1
  (JNIEnv *env, jobject obj, jint test) {
    try
    {	
	  return Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase1_helper(env,obj, test);
    }
    catch(const std::exception& ex)
    {
      std::cerr << "Error occurred: " << ex.what() << std::endl;
	  ThrowJavaRuntimeException jex(env, ex.what());
    }
    catch(...)
    {
       // catch any other errors (that we have no information about)
       std::cerr << "Unknown failure occured. Possible memory corruption" << std::endl;
	   ThrowJavaRuntimeException jex(env, "unknown");
    }
    return 1;	
}  
 
jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase1_helper
  (JNIEnv *env, jobject obj, jint test) 
{
  printf("###  Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase1, test %i\n",test);

  switch(test) {
  case 0: // unknown class
    {
    env->FindClass("org/unknown/notExistingClass");
    return 1;
    }
  case 1: // unknown method
    {
    JavaClass dataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass");
    dataClass.getMethodID("someUnknownMethod", "()V");
    return 1;
    }
  case 2: // unknown field
    {
    JavaClass dataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass");
	dataClass.getFieldID("unknownField", "I");
    return 1;
    }
  case 3: // invalid class as argument
    {
    JavaClass dataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass");
    jmethodID setStringValueMethod = dataClass.getMethodID("setStringValue", "(Ljava/lang/String;)V");
    jobject dataClassObj = dataClass.newInstance();
    if (dataClassObj==NULL) {
	printf("Failed to create object DataClass\n");
        return 1;
    }
    printf("Assiging DataClass object to a setStringValue(String). Exception expected\n");
    // Bug in OpenJdk, nothing goes wrong
    // DataClass: setStringValue, s.class: org.mahu.proto.jnitest.nativewrapper.DataClass
    env->CallVoidMethod(dataClassObj, setStringValueMethod, dataClassObj);
    return 1;
    }
  case 4: // throw exception
    // This test results in a core dump on OpenJDK.
    {
     throw std::runtime_error("Throwing cpp exception");
     return 1;
    }	
  case 5: // catch throw C++ exception
    {
    try {
      throw std::runtime_error("Throwing cpp exception");
      return -1;
    }
    catch (...) { 
      printf("5: caught thrown exception\n");
      return 1; 
    }
    }
  case 6: // throw Java Exception	
    {
	  ThrowJavaRuntimeException ex(env, "c++ error message");
      return 1;
	}
  case 7:
  case 8:
  case 9:
  {
    try
    {
      if (test==7) {
	   throw std::runtime_error("cpp exception");
	  } 
      if (test==8) {
	   throw MyException("cpp MyException");
	  } 
      if (test==9) {
	    char *ptr = 0;
		ptr[0]=0;
	  } 	  
    }
    catch(const std::runtime_error& re)
    {
       // specific handling for runtime_error
       std::cerr << "Runtime error: " << re.what() << std::endl;
	   ThrowJavaRuntimeException jex(env, re.what());
    }
    catch(const std::exception& ex)
    {
      // speciffic handling for all exceptions extending std::exception, except
      // std::runtime_error which is handled explicitly
      std::cerr << "Error occurred: " << ex.what() << std::endl;
	  ThrowJavaRuntimeException jex(env, ex.what());
    }
    catch(...)
    {
       // catch any other errors (that we have no information about)
       std::cerr << "Unknown failure occured. Possible memory corruption" << std::endl;
	   ThrowJavaRuntimeException jex(env, "unknown");
    }
	break;
  }
  case 10: // CatchException unknown class
    {
    printf("CatchException unknown class\n");
    try {
      FinallyClass fc;
      env->FindClass("org/unknown/notExistingClass");
      return -1;
    }
    catch (...) { 
      printf("10: default exception\n");
      return 1; 
    }
    }
  case 11: // JNI_assert because of null classname
  {
    try
    {	
		JavaClass noClassName(env, (jclass)0);
    }
    catch(const std::exception& ex)
    {
      std::cerr << "Error occurred: " << ex.what() << std::endl;
	  ThrowJavaRuntimeException jex(env, ex.what());
    }
    catch(...)
    {
       // catch any other errors (that we have no information about)
       std::cerr << "Unknown failure occured. Possible memory corruption" << std::endl;
	   ThrowJavaRuntimeException jex(env, "unknown");
    }
	break;
  }
  default: // unexpected
    return -1;
  }
  return 0;
}

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    testErrorCase2
 * Signature: (ILjava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase2
  (JNIEnv *env, jobject obj, jint test, jstring javaClassName_, jstring fieldOrMethodName_,  
      jstring fieldOrMethodSignature_, jobject fieldOrMethodObject_) 	  
{
  printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testErrorCase2, test %i\n",test);

  JavaStringIn javaClassName(env, javaClassName_);
  JavaStringIn fieldOrMethodName(env, fieldOrMethodName_);
  JavaStringIn fieldOrMethodSignature(env, fieldOrMethodSignature_);  
  switch(test) {
  case 0: // method
    {
	std::cout << "Calling FindClass " << javaClassName.getString() << std::endl;
    jclass javaClass = env->FindClass(javaClassName.getString());
	if (env->ExceptionCheck()) {
	   std::cout << "FindClass failed" << std::endl;
       return 1;
    }
	std::cout << "Calling GetMethodID " << fieldOrMethodName.getString() << std::endl;
    jmethodID method = env->GetMethodID(javaClass, fieldOrMethodName.getString(), fieldOrMethodSignature.getString());
	if (env->ExceptionCheck()) {
	   std::cout << "GetMethodID failed" << std::endl;
       return 1;
    }	
    break;
    }
  case 1: // field
    {
    jclass javaClass = env->FindClass(javaClassName.getString());
    jfieldID field = env->GetFieldID(javaClass, fieldOrMethodName.getString(), fieldOrMethodSignature.getString());
    break;
	}
  default: // unexpected
    return -1;
  }
  return 0;
}
  
