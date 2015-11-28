#include <jni.h>
#include <stdio.h>
#include "ThrowJavaRuntimeException.h"

ThrowJavaRuntimeException::ThrowJavaRuntimeException(JNIEnv *env, const char *message)
{
   // It is possible that there is already a pending exception in Java.
   // That exception will be throw when returning from JNI into Java space.
   if (!env->ExceptionCheck()) {
      const char *className = "java/lang/RuntimeException";
      jclass exClass = env->FindClass(className);
      if (exClass == NULL) {
		printf("ThrowJavaRuntimeException, java.lang.RuntimeException class not found \n");
      } else {
	    printf("ThrowJavaRuntimeException, going to throw exception\n");
        jint result = env->ThrowNew(exClass, message );
  	    // next line is really executed.
	    printf("ThrowJavaRuntimeException, exception has been thrown, result=%i\n", result);
	  }
	}
}

