#include <jni.h>

static jint sayHi(JNIEnv *env, jclass clazz, jstring who) {
  if (who==NULL) {
    return 0;
  }
  const char *name = env->GetStringUTFChars(who, NULL);
  if (name != NULL) {
      printf("Hello %s\n", name);
    env->ReleaseStringUTFChars(who, name);
  }
  return 0;
}

static JNINativeMethod method_table[] = {
  { "sayHi", "(Ljava/lang/String;)I", (void *) sayHi }
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
    jclass clazz = env->FindClass("org/mahu/proto/jnitest/nativewrapper/HelloJNI");
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

