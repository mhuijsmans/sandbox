/*
 * JavaClassConst.cpp
 *
 *  Created on: Aug 10, 2014
 *      Author: martien
 */

#include "JavaClassConst.h"

#define TASK_PACKAGE     "org/mahu/proto/jnitest/cpptaskexecutor/"
#define LOGGING_PACKAGE  "org/mahu/proto/jnitest/logging/"

#define TASK_PKG_CLASSSNAME(a) TASK_PACKAGE a
#define LOG_PKG_CLASSSNAME(a) LOGGING_PACKAGE a

#define GET_SIGNATURE(a) "()L" a ";"
#define SET_SIGNATURE(a) "(L" a ";)V"
#define FIELD_SIGNATURE(a) "L" a ";"

const char *JavaClassConst::CLASSNAME_TASKEXECUTOR = TASK_PKG_CLASSSNAME("TaskExecutor");
const char *JavaClassConst::FIELDNAME_TASKEXECUTOR_POINTER = "pointerCppTaskExecutor";
const char *JavaClassConst::METHODNAME_GETTASK = "getTask";
const char *JavaClassConst::SIGNATURE_GETTASK = GET_SIGNATURE ( TASK_PKG_CLASSSNAME("Task") );

const char *JavaClassConst::CLASSNAME_TASK = TASK_PKG_CLASSSNAME("Task");
const char *JavaClassConst::FIELDNAME_TASKID = "taskId";
const char *JavaClassConst::FIELDNAME_LOGGER = "logger";
const char *JavaClassConst::SIGNATURE_LOGGER = FIELD_SIGNATURE(  LOG_PKG_CLASSSNAME("LoggerProxy") );

const char *JavaClassConst::CLASSNAME_LOGGING = LOG_PKG_CLASSSNAME("LoggerProxy");
const char *JavaClassConst::METHODNAME_INFO = "info";

