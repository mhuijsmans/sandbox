#ifndef ThrowJavaRuntimeException_H_
#define ThrowJavaRuntimeException_H_

#include <jni.h>

class ThrowJavaRuntimeException
{
public:
    ThrowJavaRuntimeException(JNIEnv *env, const char *message);
};

#endif