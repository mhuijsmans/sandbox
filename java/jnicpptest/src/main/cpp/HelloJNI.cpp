#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <sstream>      // std::ostringstream
#include "org_mahu_proto_jnitest_nativewrapper_HelloJNI.h"
#include "TestHelper.h"

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    action
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_action
  (JNIEnv *env, jobject object, jint value)
{

 TestHelper helper;
 return helper.add((int)value,(int)value);
}

