#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "org_mahu_rpm_rpm_jni_NativeHelloWorldPrinter.h"

JNIEXPORT void JNICALL Java_org_mahu_rpm_rpm_1jni_NativeHelloWorldPrinter_printHelloWorld
  (JNIEnv *env, jobject obj) {
   printf("Hello World from c!\n");
   return;
}
