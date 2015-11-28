#include <jni.h>
#include <iostream>  // for std::cout
#include "org_mahu_proto_jnitest_nativewrapper_HelloJNI.h"

#include "JavaClass.h"
#include "JavaObject.h"
#include "JavaStringOut.h"
#include "JniAssert.h"

#include "ThrowJavaRuntimeException.h"

/*
 * Class:     org_mahu_proto_jnitest_nativewrapper_HelloJNI
 * Method:    logging
 * Signature: (Lorg/mahu/proto/jnitest/nativewrapper/LoggingInterface;)I
 */
 
JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper
  (JNIEnv *, jobject, jobject, jint);
  
JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging
  (JNIEnv *env, jobject obj, jobject logger, jint test) {
    try
    {	
		return JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper(env,  obj,  logger, test);
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

class LoggerBase {
public:
	LoggerBase() {}
	virtual ~LoggerBase() {}
	
	virtual void funcLog(const char * msg) {}
	virtual void traceLog(const char * msg) {}
};

class DebugLogger: LoggerBase {
public:
	DebugLogger(JavaObject &logger_) : logger(logger_){
	}
	virtual ~DebugLogger() {
	}
	
	void funcLog(const char * msg) {
	  logger.setString("functional", msg);
	}
	void traceLog(const char * msg) {
		logger.setString("trace", msg);
	}
private:
	JavaObject &logger;
};

class OptimizedDebugLogger: LoggerBase {
public:
	OptimizedDebugLogger(JNIEnv *env_, JavaObject &logger_) : env(env_), logger(logger_){
        funcMethod = logger_.getJavaClass().getMethodID("functional", "(Ljava/lang/String;)V");
	    JNI_ASSERT_NOT_NULL(funcMethod,"OptimizedDebugLogger::OptimizedDebugLogger, functional method not found");
        traceMethod = logger_.getJavaClass().getMethodID("trace", "(Ljava/lang/String;)V");
	    JNI_ASSERT_NOT_NULL(traceMethod,"OptimizedDebugLogger::OptimizedDebugLogger, trace method not found");		
	}
	virtual ~OptimizedDebugLogger() {
	}
	
	void funcLog(const char * msg) {
	   JavaStringOut javastringOut(env, msg);
	   env->CallVoidMethod(logger, funcMethod, javastringOut.getString());
	   JNI_CHECK_EXCEPTION;	
	}
	void traceLog(const char * msg) {
	   JavaStringOut javastringOut(env, msg);
	   env->CallVoidMethod(logger, traceMethod, javastringOut.getString());
	   JNI_CHECK_EXCEPTION;
	}
private:
    JNIEnv *env;
	JavaObject &logger;
	jmethodID funcMethod;
	jmethodID traceMethod;
};

class BufferedLogger: DebugLogger {
public:
	BufferedLogger(JavaObject &logger_) : DebugLogger(logger_), funcBuffer(), traceBuffer() {
		funcBuffer.reserve(4096);
		traceBuffer.reserve(4096);
	}
	virtual ~BufferedLogger() {
		if (funcBuffer.length()>0) {
		  DebugLogger::funcLog(funcBuffer.c_str());
		}
		if (traceBuffer.length()>0) {
			DebugLogger::traceLog(traceBuffer.c_str());
		}
	}
	
	void funcLog(const char * msg) {
	    if (funcBuffer.length()>0) {
			funcBuffer.append("\n");
		}
		funcBuffer.append(msg);
	}
	void traceLog(const char * msg) {
		if (traceBuffer.length()>0) {
			traceBuffer.append("\n");
		}
		traceBuffer.append(msg);
	}
private:
    std::string funcBuffer;
	std::string traceBuffer;
};

JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper_0
  (JNIEnv *env_, jobject obj_, jobject logger_) {
    
  JavaObject logger(env_,"org/mahu/proto/jnitest/nativewrapper/LoggingInterface", logger_);
  logger.setString("functional", "&&&&&&&&&&&&&&&&&&&&");
  logger.setString("trace", "@@@@@@@@@@@@@@@@@@@@@@");
  
  BufferedLogger bufLogger(logger);
  bufLogger.funcLog("Buffered, functional log message-1");
  bufLogger.funcLog("Buffered, functional log message-2");
  bufLogger.traceLog("Buffered, trace log message-1");  
  bufLogger.traceLog("Buffered, trace log message-2");  
  
  DebugLogger dbgLogger(logger);
  dbgLogger.funcLog("A functional log message");
  dbgLogger.traceLog("A trace log message");
  
  return 0;
}

JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper_1
  (JNIEnv *env_, jobject obj_, jobject logger_) {
 
  JavaObject loggerObj(env_,"org/mahu/proto/jnitest/nativewrapper/LoggingInterface", logger_);
  DebugLogger logger(loggerObj);
  for(int i=0; i< 100000; i++) {
	logger.funcLog("a");
  }  
  return 0;
}

JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper_2
  (JNIEnv *env_, jobject obj_, jobject logger_) {
 
  JavaObject loggerObj(env_,"org/mahu/proto/jnitest/nativewrapper/LoggingInterface", logger_);
  OptimizedDebugLogger logger(env_, loggerObj);
  for(int i=0; i< 100000; i++) {
	logger.funcLog("a");
  }  
  return 0;
}

JNIEXPORT jint JNICALL Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper
  (JNIEnv *env_, jobject obj_, jobject logger_, jint test) {
  std::cout << "@@@ Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging, test " << test << std::endl;
  
  switch(test) {
  case 0: 
	return Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper_0(env_,  obj_,  logger_);
  case 1: // 100.000 log messages
	return Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper_1(env_,  obj_,  logger_);	
  case 2: // 100.000 log messages
	return Java_org_mahu_proto_jnitest_nativewrapper_HelloJNI_logging_helper_2(env_,  obj_,  logger_);		
  default:
	return 1;
  }
  return 1;
}