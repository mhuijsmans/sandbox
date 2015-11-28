#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <sstream>      // std::ostringstream
#include <iostream>  // for std::cout
#include "org_mahu_proto_jnitest_nativewrapper_HelloJNI.h"

#include "JavaClass.h"
#include "JavaObject.h"
#include "JByteArrayPtrIn.h"
#include "JByteArrayPtrOut.h"
#include "JavaStringIn.h"
#include "JavaStringOut.h"
#include "ThrowJavaRuntimeException.h"

#include "ByteBuffer.h"

// ###########################################################################################

JNIEXPORT jstring JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_addHelloPrefix_helper
  (JNIEnv * env, jobject obj, jstring inJNIStr) {
  
   printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_addHelloPrefix\n");
   
   if (inJNIStr == NULL) return NULL;
   JavaStringIn stringIn(env, inJNIStr);  
   
   // Constructs a new java.lang.String object from an array of characters in modified UTF-8 encoding.
   jsize stringLength = stringIn.getLength();
   
   char outCStr[stringLength+10];
   strcpy(outCStr, "Hello");  // must be <=9, because of terminating 0
   size_t len = strlen(outCStr);
   // todo: "solve: warning: conversion to ‘size_t’ from ‘jsize’ may change the sign   
   strncpy(outCStr+len,stringIn.getString(),stringLength);
   outCStr[stringLength+len]=0;
   
   JavaStringOut javastringOut(env, outCStr);
   return javastringOut;
} 

JNIEXPORT jstring JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_addHelloPrefix
  (JNIEnv * env, jobject obj, jstring inJNIStr) {
    try
    {	
		return JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_addHelloPrefix_helper(env,  obj,  inJNIStr);
    }
    catch(const std::exception& ex)
    {
      std::cerr << "Error occurred: " << ex.what() << std::endl;
	  ThrowJavaRuntimeException jex(env, ex.what());
    }
    catch(...)
    {
       // catch any other errors (that we have no information about)
       std::cerr << "Unknown failure occured. Possible memory corruption" << std::endl;
	   ThrowJavaRuntimeException jex(env, "unknown");
    }
	return NULL;
} 

// ###########################################################################################
 
JNIEXPORT jbyteArray JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processBytes
  (JNIEnv *env, jobject obj, jbyteArray bytes) {
  
  printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processBytes\n");  
  
  if (NULL == bytes) return NULL;   
  
  JByteArrayPtrIn jByteArrayPtrIn(env,bytes); 
  jsize lengthOfArray = jByteArrayPtrIn.getNumberOfBytes();
  
  if (lengthOfArray> 20*1000*1000) return NULL;
  
  ByteBuffer buffer(lengthOfArray+1); 
  jbyte * buf = (jbyte *)buffer.getBuffer();
  int i=0;
  for(i=0; i< lengthOfArray+1; i++) {
  	buf[i] = (jbyte)(i+3);
  }  
  
  JByteArrayPtrOut jByteArrayPtrOut(env, buf, lengthOfArray+1); 
  return jByteArrayPtrOut; 
}

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    returnDataClass
 * Signature: ()Lorg/mahu/proto/jnitest/nativewrapper/DataClass;
 */
JNIEXPORT jobject JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_returnDataClass
  (JNIEnv *env, jobject obj) {
  
  printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_returnDataClass\n");  
  //
  JavaObject dataClassOut(env, "org/mahu/proto/jnitest/nativewrapper/DataClass");
  printf("Found default constructor & created instance DataClass\n");
  //
  printf("Calling method: setIntValue()\n");
  int newValue = 100;  
  dataClassOut.setInt("setIntValue", newValue);
  // 
  return dataClassOut.getJObject();
}
 
 /*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    processDataClass
 * Signature: (Lorg/mahu/proto/jnitest/nativewrapper/DataClass;)V
 */
JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass
  (JNIEnv *env, jobject obj, jobject dataClassObj) {
  
  printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass\n");   

  JavaObject javaObjectDataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass", dataClassObj);  
 
  jboolean boolValue =  javaObjectDataClass.getBoolean("getBooleanValue");
  javaObjectDataClass.setBoolean("setBooleanValue",true);
 
  jint val = javaObjectDataClass.getInt("getIntValue"); 
  if (boolValue) {
		printf("Method getIntValue() return: %i\n", val);
  }
  javaObjectDataClass.setInt("setIntValue",val*2);
  
  JavaStringIn str(env,javaObjectDataClass.getString("getStringValue"));
  std::ostringstream os;
  os << str.getString() << "-jni" << "\0";
  javaObjectDataClass.setString("setStringValue", os.str());
  
  jfloat f = javaObjectDataClass.getFloat("getFloatValue");
  javaObjectDataClass.setFloat("setFloatValue",f * 3);
  
  jdouble d = javaObjectDataClass.getDouble("getDoubleValue");
  javaObjectDataClass.setDouble("setDoubleValue",d/2);  
  
  JByteArrayPtrIn byteArray(env, javaObjectDataClass.getBytes("getByteArrayValue"));
  if (byteArray.getNumberOfBytes()>0) 
  {
    int nrOfBytes = byteArray.getNumberOfBytes()*2;
    jbyte buf[nrOfBytes];
    javaObjectDataClass.setBytes("setByteArrayValue", buf, nrOfBytes);
  }
}

