#ifndef JNI_ASSERT_H_
#define JNI_ASSERT_H_

#include <stdexcept>

#define JNI_ASSERT(a) { throw std::runtime_error(a); } 
#define JNI_ASSERT_NOT_NULL(a,b) if (a == NULL) { throw std::runtime_error(b); } 
#define JNI_ASSERT_NOT_ZERO(a,b) if (!a) { throw std::runtime_error(b); } 
#define JNI_CHECK_EXCEPTION if (env->ExceptionCheck()) { throw std::runtime_error("exception pending in java"); } 

#endif 