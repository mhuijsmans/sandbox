#ifndef TaskExecutor_H_
#define TaskExecutor_H_

#include <jni.h>
#include "LoggerBase.h"

class TaskExecutor
final
{
	public:
		TaskExecutor(JNIEnv *env, JavaObject &javaTaskExecutor);
		~TaskExecutor();
		// Don't use the copy constructor
		TaskExecutor(const TaskExecutor &rhs);
		// Don't use the = operator
		TaskExecutor & operator=(const TaskExecutor &rhs);
		void execute(JNIEnv *env, JavaObject &javaTaskExecutor, LoggerBase &logger);

	private:
		enum Tasks { NO_OP_TASK=0, TASK1=1, TASK2=2 };
	};

#endif
