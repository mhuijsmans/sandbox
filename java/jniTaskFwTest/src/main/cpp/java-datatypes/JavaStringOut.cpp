#include <stdexcept>
#include "JavaStringOut.h"

JavaStringOut::JavaStringOut(JNIEnv *env_, const char *outCStr_)
   {
     env = env_;
	 javaString = NULL;
     // Convert the C-string (char*) into JNI String (jstring)
	 if (outCStr_!=0) {
       javaString = env->NewStringUTF(outCStr_);
	 }
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
 