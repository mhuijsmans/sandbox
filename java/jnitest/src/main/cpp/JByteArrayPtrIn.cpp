#include <stdexcept>
#include <iostream>
#include "JByteArrayPtrIn.h"
#include "JniAssert.h"

JByteArrayPtrIn::JByteArrayPtrIn(JNIEnv *env_, jbyteArray bytes_)
   {
     env = env_;
     bytes = bytes_;
     bufferPtr = NULL;
     lengthOfArray = 0;
	 jboolean isCopy = false;	 
	 JNI_ASSERT_NOT_NULL(bytes_,"JByteArrayPtrIn::JByteArrayPtrIn, bytes is null");
	 // jbyte is "signed 8 byte"
     // ref: http://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/types.html 
     bufferPtr = env->GetByteArrayElements(bytes, &isCopy);
	 JNI_CHECK_EXCEPTION;
     if (bufferPtr!=NULL) {
       lengthOfArray = env->GetArrayLength(bytes);
	   JNI_CHECK_EXCEPTION;
     }
	 std::cout << "JByteArrayPtrIn::JByteArrayPtrIn: value isCopy: " << (isCopy ? "true" : "false") << " length: " << lengthOfArray << std::endl;
   } 
   
JByteArrayPtrIn::JByteArrayPtrIn(const JByteArrayPtrIn &rhs) 
{
  throw std::runtime_error("Don't use this constructor");
}

JByteArrayPtrIn::~JByteArrayPtrIn()
   {
     // The last parameter means that the JVM will not copy the bytes back 
     // from bufferPtr to the Java array
     if (bufferPtr!=NULL ) {
        // printf("JByteArrayPtrIn.dtor() ReleaseByteArrayElements(..)\n");
        env->ReleaseByteArrayElements(bytes, bufferPtr, JNI_ABORT);
        JNI_CHECK_EXCEPTION;
     }
   }

JByteArrayPtrIn& JByteArrayPtrIn::operator=(const JByteArrayPtrIn &rhs)
{
  throw std::runtime_error("Don't use this operator");
}

jbyte* JByteArrayPtrIn::getBytePtr() {
     return bufferPtr;
   }
  
jsize JByteArrayPtrIn::getNumberOfBytes() {
     return lengthOfArray;
   }   