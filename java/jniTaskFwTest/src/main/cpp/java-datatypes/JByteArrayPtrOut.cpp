#include <stdexcept>
#include "JByteArrayPtrOut.h"
#include "JniAssert.h"

JByteArrayPtrOut::JByteArrayPtrOut(JNIEnv *env_, const jbyte * bytes_, const int nrOfBytes_)
   {
     env = env_;
	 if (nrOfBytes_>=0) {
	   JNI_ASSERT_NOT_NULL(bytes_,"JByteArrayPtrOut::JByteArrayPtrOut, null byte pointer with length > 0"); 
	   result=env->NewByteArray(nrOfBytes_);
	   JNI_CHECK_EXCEPTION;
	   // Next method can be used for new and existing byte[].
	   // For existing, check length of byte[]
       env->SetByteArrayRegion (result, 0, nrOfBytes_, bytes_);
	   JNI_CHECK_EXCEPTION;
	 }
   } 
   
JByteArrayPtrOut::JByteArrayPtrOut(const JByteArrayPtrOut &rhs) 
{
  throw std::runtime_error("Don't use this constructor");
}

JByteArrayPtrOut::~JByteArrayPtrOut()
   {
	// empty
   }

JByteArrayPtrOut& JByteArrayPtrOut::operator=(const JByteArrayPtrOut &rhs)
{
  throw std::runtime_error("Don't use this operator");
}

jbyteArray JByteArrayPtrOut::getJByteArray() {
     return result;
   }
