/*
 * TaskNoOp.cpp
 *
 *  Created on: Aug 10, 2014
 *      Author: martien
 */
#include <iostream>  // for std::cout
#include "TaskNoOp.h"
#include "JavaStringIn.h"

TaskNoOp::TaskNoOp() {
	// TODO Auto-generated constructor stub

}

TaskNoOp::~TaskNoOp() {
	// TODO Auto-generated destructor stub
}

void TaskNoOp::execute(JNIEnv *env, JavaObject &javaTaskExecutor,
		JavaObject &task, LoggerBase &logger) {
	logger.traceLog("TaskNoOp::execute");

//	try {
//		// Accessing a function defined in derived class of Task
//		jstring stringRef = task.getString("getNoOpString");
//		JavaStringIn msg(env, stringRef);
//		logger.traceLog(std::string("TaskNoOp::execute: ") + msg.getString());
//	} catch (const std::exception& ex) {
//		logger.traceLog(std::string("TaskNoOp::execute, error occurred: ")+ex.what());
//		// Clear the JavaException; That is needed on JNI interface.
//		env->ExceptionClear();
//	}
	// other exceptions not caught
}

