#include <stdexcept>
#include "JavaStringOut.h"
#include "JniAssert.h"

JavaStringOut::JavaStringOut(JNIEnv *env_, const char *outCStr_)
   {
     env = env_;
	 javaString = NULL;
     JNI_ASSERT_NOT_NULL(outCStr_,"JavaStringOut::JavaStringOut, outCStr is null");	 
     // Convert the C-string (char*) into JNI String (jstring)
     javaString = env->NewStringUTF(outCStr_);
	 JNI_CHECK_EXCEPTION;
   } 
   
JavaStringOut::JavaStringOut(const JavaStringOut &rhs) 
{
  throw std::runtime_error("Don't use this constructor");
}

JavaStringOut::~JavaStringOut()
{
	// empty
}

JavaStringOut& JavaStringOut::operator=(const JavaStringOut &rhs)
{
  throw std::runtime_error("Don't use this operator");
}

jstring JavaStringOut::getString() 
{
	return javaString;
}
 