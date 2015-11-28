#include <stdexcept>
#include <list>
#include <jni.h>

static std::list<unsigned char *> mylist; 

static jint BigObjectNativeCall(JNIEnv *env, jclass clazz, jint size) {
  //printf("Hello \n");
  // allocate, do not release
  unsigned char * data = new unsigned char[size];
  if (!data) {
	throw std::runtime_error("Failed to allocate memory");
  } else {
     mylist.push_back (data);
  }
  return 0;
}

static JNINativeMethod method_table[] = {
  { "call", "(I)I", (void *) BigObjectNativeCall }
};

static int method_table_size = sizeof(method_table) / sizeof(method_table[0]);

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  printf("JNI_OnLoad ENTER");
  JNIEnv* env;
  if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    printf("JNI_OnLoad LEAVE: returning  JNI_ERR");
    return JNI_ERR;
  } else {
    printf("JNI_OnLoad looking up class");
    jclass clazz = env->FindClass("org/mahu/proto/bigobjects/BigObjectNative");
    if (clazz) {
      jint ret = env->RegisterNatives(clazz, method_table, method_table_size);
      env->DeleteLocalRef(clazz);
	  printf("JNI_OnLoad LEAVE, registeredNatives, status %i\n",ret);
      return ret == 0 ? JNI_VERSION_1_6 : JNI_ERR;
    } else {
	  printf("JNI_OnLoad LEAVE, failed to find class");
      return JNI_ERR;
    }
  }
}

