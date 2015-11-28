#include <jni.h>
#include <sstream>      // std::ostringstream
#include <iostream>  // for std::cout
#include <execinfo.h> // backtrace
#include "unistd.h" // STDERR_FILENO, usleep
#include <stdlib.h> // exit

#include "org_mahu_proto_jnitest_nativewrapper_HelloJNI.h"

#include "JavaClass.h"
#include "JavaObject.h"
#include "JniAssert.h"

#include "JavaIntField.h"
//#include "JavaStringField.h"
#include "JavaByteArrayField.h"
#include "JavaBooleanField.h"
#include "JavaDoubleField.h"

#include "JavaIntMethod.h"
#include "JByteArrayPtrIn.h"
#include "JavaStringIn.h"

#include "ThrowJavaRuntimeException.h"
#include <signal.h>

class MyClass
{
public:
  MyClass(JNIEnv *env_) : 
    env(env_),
	javaClass(env_, "org/mahu/proto/jnitest/nativewrapper/HelloJNI"),
    test(env_, javaClass.getClass(), "test"),
    intMethod(env_, javaClass.getClass(), "IntValue") {
  }
  
  MyClass(JNIEnv *env_, MyClass &myClass, jobject obj) : 
    env(env_),
	javaClass(env_, myClass.javaClass),
    test(env_, myClass.test, obj),
    intMethod(env_, myClass.intMethod, obj)	{
  }  
  
  // I want test to be public. C++ initializes members in the define order.
  // So I choose to make all members public.
  // ref: http://stackoverflow.com/questions/1828037/whats-the-point-of-g-wreorder
  JNIEnv *env;
  JavaClass javaClass;
  JavaIntField test; 
  JavaIntMethod intMethod; 
};

class MyDataClass1
{
public:
  MyDataClass1(JNIEnv *env_) : 
    env(env_),
	javaClass(env_, "org/mahu/proto/jnitest/nativewrapper/DataClass1"),
    intField(env_, javaClass.getClass(), "intValue"), 
	//stringField(env_, javaClass.getClass(), "stringValue"),
	byteArrayField(env_, javaClass.getClass(), "byteArrayValue"),
	booleanField(env_, javaClass.getClass(), "booleanValue"),
	doubleField(env_, javaClass.getClass(), "doubleValue") {
  }
  
  MyDataClass1(JNIEnv *env_, MyDataClass1 &other, jobject obj) : 
    env(env_),
	javaClass(env_, other.javaClass),
    intField(env_, other.intField, obj),
	//stringField(env_, other.test, obj),
	byteArrayField(env_, other.byteArrayField, obj),
	booleanField(env_, other.booleanField, obj),
	doubleField(env_, other.doubleField, obj) {
  }  
  
  // I want test to be public. C++ initializes members in the define order.
  // So I choose to make all members public.
  // ref: http://stackoverflow.com/questions/1828037/whats-the-point-of-g-wreorder
  JNIEnv *env;
  JavaClass javaClass;
  JavaIntField intField; 
  //JavaStringField stringField; 
  JavaByteArrayField byteArrayField; 
  JavaBooleanField booleanField; 
  JavaDoubleField doubleField; 
};

MyClass *myGlobalClass;
MyDataClass1 *myMyDataClass1;

void handler(int sig)
{
  void *array[20];
  size_t size;

  // get void*'s for all entries on the stack
  size = backtrace(array, 20);

  // print out all the frames to stderr
  fprintf(stderr, "Error: signal %d:\n", sig);
  backtrace_symbols_fd(array, size, STDERR_FILENO);

  fprintf(stderr, "Sleeping for 3 seconds waiting for buffers to clear");
  usleep(3000);

  exit(1);
}

// baz() generates a segv
void baz() {
 int *foo = (int*)-1; // make a bad pointer
  printf("%d\n", *foo);       // causes segfault
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  std::cout << "JNI_ONLOAD enter" << std::endl;
  JNIEnv* env;
  if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    std::cout << "JNI_OnLoad LEAVE: returning  JNI_ERR" << std::endl;
    return JNI_ERR;
  } else {
    try
    {	
	  std::cout << "JNI_OnLoad resolving class" << std::endl;
      myGlobalClass = new MyClass(env);
	  myMyDataClass1 = new MyDataClass1(env);
    }
    catch(...)
    {
       std::cout << "Unknown failure occurred. Possible memory corruption" << std::endl;
	   return JNI_ERR;
    }
// With a signal handler installed you do not get the core dump files.
// So I see no value in having a handler (right now)..
// http://www.cplusplus.com/reference/csignal/signal/
//   signal(SIGFPE,  handler);   // Signal Floating-Point Exception, e.g. divide by zero
//   signal(SIGSEGV, handler);   // signal segmentation violation
// sigaction: http://stackoverflow.com/questions/17942034/simple-linux-signal-handling
	return JNI_VERSION_1_6 ;
  }
}

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    testSpecial
 * Signature: (I)I
 */
jint Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testSpecial_helper
  (JNIEnv *env, jobject obj, jint test) 
{
  std::cout << "###  Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testSpecial, test " << test << std::endl;

  switch(test) {
  case 0: // call method on object that returns a string. 
    {
	  JavaObject javaObjectDataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass");
	  jobject stringObj = javaObjectDataClass.getObject("getStringValue","()Ljava/lang/String;"); 
	  JavaObject stringJavaObj(env, "java/lang/String",stringObj);
	  jint length = stringJavaObj.getInt("length");
	  std::cout << "Length of string is: " << length << std::endl;
      return 0;
    }
  case 1: // call field on object that returns a string. 
    {
	  JavaObject javaObjectDataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass1");
	  jobject stringObj = javaObjectDataClass.getObjectField("stringValue","Ljava/lang/String;"); 
	  JavaObject stringJavaObj(env, "java/lang/String",stringObj);
	  jint length = stringJavaObj.getInt("length");
	  std::cout << "Length of string is: " << length << std::endl;
      return 0;
    }	
	case 2:
	{
	  MyClass myClass(env, *myGlobalClass, obj);
	  int value = myClass.test.get()+10;
	  myClass.test.set(value);
	  return myClass.test.get();
	}
	case 3: // JNI_SPECIAL_CASE_MYCLASS_GET_METHOD_INT
	{
	  MyClass myClass(env, *myGlobalClass, obj);
	  int value = myClass.intMethod.get()+10;
	  myClass.intMethod.set(value);
	  return myClass.intMethod.get();
	}	
	case 4: // JNI_SPECIAL_CASE_GET_SIZEOF_VOID_POINTER
	{
	  return sizeof(void *);
	}	
	case 5: // JNI_SPECIAL_CASE_GET_SIZEOF_JLONG
	{
	  return sizeof(jlong);
	}		
	case 6: // JNI_SPECIAL_CASE_LOAD_CLASS
	{
      JavaObject javaObjectDataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass2");
	  jint intValue =  javaObjectDataClass.getIntField("intValue");
	  return intValue;
	}	
	case 7: // SEGV
	{
	  // baz();
	  return 1;
	}
	case 8: // JNI_SPECIAL_CASE_PRINT_OBJECTREF
	{
       std::cout << "HelloJNI objRef " << std::hex << std::showbase << obj << std::dec << std::endl;
	   for(int i=0; i<100; i++) {
         JavaObject javaObjectDataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass2");	   
		 std::cout << "DataClass2 objRef " << std::hex << std::showbase 
				<< javaObjectDataClass.getJObject() << std::dec << std::endl;
	   }
	   break;
	}	
	case 9: // JNI_SPECIAL_CASE_BIG_ARRAY_READ
	{
		JavaObject javaObject(env, "org/mahu/proto/jnitest/nativewrapper/HelloJNI", obj);
		JByteArrayPtrIn byteArray(env, javaObject.getBytesField("specialBytes"));	
	    return 1;
	}
	default:
	  break;
  }
  return 1;
}
  
JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testSpecial
  (JNIEnv *env, jobject obj, jint test)
{
    try
    {	
		return JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_testSpecial_helper(env,  obj,  test);
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
	return 1;
}

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    processDataClass1
 * Signature: (Lorg/mahu/proto/jnitest/nativewrapper/DataClass1;)V
 */
/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    processDataClass1
 * Signature: (Lorg/mahu/proto/jnitest/nativewrapper/DataClass1;)V
 */
JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1_a
  (JNIEnv *env, jobject obj, jobject dataClass1Obj);
JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1_b
  (JNIEnv *env, jobject obj, jobject dataClass1Obj);  
 
JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1
  (JNIEnv *env, jobject obj, jobject dataClass1Obj) {
  
    try
    {	
	  Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1_b(env,obj,dataClass1Obj);
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
  
}

JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1_b
  (JNIEnv *env, jobject obj, jobject dataClass1Obj) {
  
  printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1\n"); 
  
  MyDataClass1 dc1(env, *myMyDataClass1, dataClass1Obj);

  jboolean boolValue =  dc1.booleanField.get();
  dc1.booleanField.set(true);
  
  jint intValue =  dc1.intField.get();
  dc1.intField.set(intValue*2);

//  JavaStringIn str(env,javaObjectDataClass.getStringField("stringValue"));
//  std::ostringstream os;
//  os << str.getString() << "-jni" << "\0";
//  javaObjectDataClass.setStringField("stringValue", os.str());
  
//  jfloat f = javaObjectDataClass.getFloatField("floatValue");
//  javaObjectDataClass.setFloatField("floatValue",f * 3);  
  
  jdouble d = dc1.doubleField.get();
  dc1.doubleField.set(d/2);   

  JByteArrayPtrIn byteArray(env, dc1.byteArrayField.get());
  if (byteArray.getNumberOfBytes()>0) 
  {
    int nrOfBytes = byteArray.getNumberOfBytes()*2;
    jbyte buf[nrOfBytes];
	dc1.byteArrayField.set(buf, nrOfBytes);
  }  
}
 
JNIEXPORT void JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1_a
  (JNIEnv *env, jobject obj, jobject dataClass1Obj) {

  printf("### Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_processDataClass1\n"); 
  
  JavaObject javaObjectDataClass(env,"org/mahu/proto/jnitest/nativewrapper/DataClass1", dataClass1Obj);

  jboolean boolValue =  javaObjectDataClass.getBooleanField("booleanValue");
  javaObjectDataClass.setBooleanField("booleanValue",true);
  
  jint intValue =  javaObjectDataClass.getIntField("intValue");
  javaObjectDataClass.setIntField("intValue",intValue*2);

  JavaStringIn str(env,javaObjectDataClass.getStringField("stringValue"));
  std::ostringstream os;
  os << str.getString() << "-jni" << "\0";
  javaObjectDataClass.setStringField("stringValue", os.str());
  
  jfloat f = javaObjectDataClass.getFloatField("floatValue");
  javaObjectDataClass.setFloatField("floatValue",f * 3);  
  
  jdouble d = javaObjectDataClass.getDoubleField("doubleValue");
  javaObjectDataClass.setDoubleField("doubleValue",d/2);   

  JByteArrayPtrIn byteArray(env, javaObjectDataClass.getBytesField("byteArrayValue"));
  if (byteArray.getNumberOfBytes()>0) 
  {
    int nrOfBytes = byteArray.getNumberOfBytes()*2;
    jbyte buf[nrOfBytes];
    javaObjectDataClass.setBytesField("byteArrayValue", buf, nrOfBytes);
  }
}
