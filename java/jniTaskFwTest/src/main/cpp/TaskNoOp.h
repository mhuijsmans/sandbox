/*
 * TaskNoOp.h
 *
 *  Created on: Aug 10, 2014
 *      Author: martien
 */

#ifndef TASKNOOP_H_
#define TASKNOOP_H_

#include <jni.h>
#include "JavaObject.h"
#include "LoggerBase.h"

class TaskNoOp {
public:
	TaskNoOp();
	virtual ~TaskNoOp();
	// I do not fully understand const when used.
	void execute(JNIEnv *env,
			JavaObject &javaTaskExecutor, JavaObject &task, LoggerBase &logger);
};

#endif /* TASKNOOP_H_ */
