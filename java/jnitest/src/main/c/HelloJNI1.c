#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "org_mahu_proto_jnitest_nativewrapper_HelloJNI.h"

// Implementation of native method void printHello() 
// of org.mahu.proto.jnitest.nativewrapper.HelloJNI class
JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_printHello
  (JNIEnv *env, jobject obj) {
   printf("Hello World!\n");
   return;
}

JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_addValues
  (JNIEnv * env, jobject obj, jint i1, jint i2) {
  return i1+i2;
 }

