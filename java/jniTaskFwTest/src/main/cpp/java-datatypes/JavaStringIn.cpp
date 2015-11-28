#include <stdexcept>
#include "JavaStringIn.h"
#include "JniAssert.h"

JavaStringIn::JavaStringIn(JNIEnv *env_, jstring inJNIStr_)
   {
   	 JNI_ASSERT_NOT_NULL(inJNIStr_,"JavaStringIn::JavaStringIn string is null");
     env = env_;
     inJNIStr = inJNIStr_;
     inCStr = 0;
     lengthOfString = 0;
     // isCopy (in GetStringUTFChars(..)) is an "in-out" parameter.
     // It will be set to JNI_TRUE if the returned string is a copy of the original java.lang.String instance. 
     // It will be set to JNI_FALSE if the returned string is a direct pointer to the original String instance,
     //    in this case, the native code shall not modify the contents of the returned string. 
     // The JNI runtime will try to return a direct pointer, if possible; otherwise, it returns a copy. 
     jboolean isCopy;	 
     if (env!=NULL && inJNIStr!=NULL) {
        inCStr = env->GetStringUTFChars( inJNIStr, &isCopy);
        if (inCStr!=NULL) {
           lengthOfString = env->GetStringUTFLength( inJNIStr);;
        }
     }
   } 
   
JavaStringIn::JavaStringIn(const JavaStringIn &rhs) 
{
  throw std::runtime_error("JavaStringIn::JavaStringIn, don't use this constructor");
}

JavaStringIn::~JavaStringIn()
   {
     if (inCStr!=NULL ) {
        // printf("JavaStringIn.dtor() ReleaseStringUTFChars(..)\n");
        env->ReleaseStringUTFChars(inJNIStr, inCStr);
     }
   }

JavaStringIn& JavaStringIn::operator=(const JavaStringIn &rhs)
{
  throw std::runtime_error("JavaStringIn& JavaStringIn::operator=, don't use this operator");
}

bool JavaStringIn::isNull() {
	return inJNIStr == NULL;
}

const char * JavaStringIn::getString() {
     return inCStr;
   }
  
jsize JavaStringIn::getLength() {
     return lengthOfString;
   }   