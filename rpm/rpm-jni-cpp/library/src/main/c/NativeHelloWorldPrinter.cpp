#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "org_mahu_rpm_rpm_jni_NativeHelloWorldPrinter.h"
#include "Derived.h"

JNIEXPORT void JNICALL Java_org_mahu_rpm_rpm_1jni_NativeHelloWorldPrinter_printHelloWorld
  (JNIEnv *env, jobject obj) {
   printf("Hello World from cpp!\n");
   
   baselib::impl::Derived derived;
   derived.Method1();
  
   return;
}
