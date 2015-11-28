#include <iostream>  // for std::cout#include <jni.h>
#include "JavaObject.h"
#include "ThrowJavaRuntimeException.h"
#include "JniAssert.h"
#include "TaskExecutor.h"
#include "JavaClassConst.h"#include "TaskExecutorJNI.h"#include "DirectLogger.h"TaskExecutor* getTaskExecutorPointer(JavaObject &javaTaskExecutor);void setTaskExecutorPointer(JavaObject &javaTaskExecutor, jlong value);

void initCpp(JNIEnv *env, jobject javaTaskExecutorRef) {
	try {
		JavaObject javaTaskExecutor(env, JavaClassConst::CLASSNAME_TASKEXECUTOR,
				javaTaskExecutorRef);		//		jobject loggerRef = javaTaskExecutor.getObjectField(JavaClassConst::FIELDNAME_LOGGER, JavaClassConst::SIGNATURE_LOGGER);		JavaObject loggerObj(env, JavaClassConst::CLASSNAME_LOGGING, loggerRef);		DirectLogger logger(loggerObj);		//		logger.traceLog("TaskExecutor-initCpp ENTER");		//
		TaskExecutor * taskExecutor = new TaskExecutor(env, javaTaskExecutor);		setTaskExecutorPointer(javaTaskExecutor, (jlong) taskExecutor);		//
		logger.traceLog("TaskExecutor-initCpp LEAVE");
	} catch (const std::exception& ex) {
		std::cerr << "TaskExecutor-initCpp: error occurred: " << ex.what() << std::endl;
		ThrowJavaRuntimeException jex(env, ex.what());
	} catch (...) {
		// catch any other errors (that we have no information about)
		std::cerr << "Unknown failure occured. Possible memory corruption"
				<< std::endl;
		ThrowJavaRuntimeException jex(env, "unknown");
	}
}

void executeCpp(JNIEnv *env, jobject javaTaskExecutorRef) {
	try {
		JavaObject javaTaskExecutor(env, JavaClassConst::CLASSNAME_TASKEXECUTOR,
				javaTaskExecutorRef);		//		jobject loggerRef = javaTaskExecutor.getObjectField(JavaClassConst::FIELDNAME_LOGGER, JavaClassConst::SIGNATURE_LOGGER);		JavaObject loggerObj(env, JavaClassConst::CLASSNAME_LOGGING, loggerRef);		DirectLogger logger(loggerObj);		//		logger.traceLog("TaskExecutor-executeCpp ENTER");		//
		TaskExecutor* taskExecutor = getTaskExecutorPointer(javaTaskExecutor);
		taskExecutor->execute(env, javaTaskExecutor, logger);		//		logger.traceLog("TaskExecutor-executeCpp LEAVE");
	} catch (const std::exception& ex) {
		std::cerr << "TaskExecutor-executeCpp: error occurred: " << ex.what() << std::endl;
		ThrowJavaRuntimeException jex(env, ex.what());
	} catch (...) {
		// catch any other errors (that we have no information about)
		std::cerr << "executeCpp: unknown failure occured. Possible memory corruption"
				<< std::endl;
		ThrowJavaRuntimeException jex(env, "unknown");
	}
}void disposeCpp(JNIEnv *env, jobject javaTaskExecutorRef) {
	try {
		JavaObject javaTaskExecutor(env, JavaClassConst::CLASSNAME_TASKEXECUTOR,				javaTaskExecutorRef);		//		jobject loggerRef = javaTaskExecutor.getObjectField(JavaClassConst::FIELDNAME_LOGGER, JavaClassConst::SIGNATURE_LOGGER);		JavaObject loggerObj(env, JavaClassConst::CLASSNAME_LOGGING, loggerRef);		DirectLogger logger(loggerObj);		//		logger.traceLog("TaskExecutor-disposeCpp ENTER");		//		TaskExecutor* taskExecutor = getTaskExecutorPointer(javaTaskExecutor);
		setTaskExecutorPointer(javaTaskExecutor,0);
		delete taskExecutor;		//		logger.traceLog("TaskExecutor-disposeCpp LEAVE");
	} catch (const std::exception& ex) {
		std::cerr << "TaskExecutor-disposeCpp, error occurred: " << ex.what() << std::endl;
		ThrowJavaRuntimeException jex(env, ex.what());
	} catch (...) {
		// catch any other errors (that we have no information about)
		std::cerr << "Unknown failure occurred. Possible memory corruption"
				<< std::endl;
		ThrowJavaRuntimeException jex(env, "unknown");
	}
}TaskExecutor* getTaskExecutorPointer(JavaObject &javaTaskExecutor) {	jlong taskExecutorPointer = javaTaskExecutor.getLongField(			JavaClassConst::FIELDNAME_TASKEXECUTOR_POINTER);	JNI_ASSERT_NOT_ZERO(taskExecutorPointer,			"taskExecutorPointer is null; program error");//	std::cout << "taskExecutorPointer: " << std::hex << taskExecutorPointer//			<< std::dec << std::endl;	return (TaskExecutor*) (taskExecutorPointer);}void setTaskExecutorPointer(JavaObject &javaTaskExecutor, jlong value) {//	std::cout << "taskExecutorPointer: " << std::hex << value//			<< std::dec << std::endl;	javaTaskExecutor.setLongField(			JavaClassConst::FIELDNAME_TASKEXECUTOR_POINTER, value);}

