/*
 * Filename: SysLogExplorer.h
 */

#ifndef SYSLOGEXPLORER_H_
#define SYSLOGEXPLORER_H_

class SysLogExplorer {
public:
	static void SysLogOneCall();
	static void SysLogManyCalls();
private:
	SysLogExplorer();
	virtual ~SysLogExplorer();

	static void MakeManySyslogCalls(const int max);
	static void MakeOneSyslogCalls();

	static const char *ident;
};

#endif /* SYSLOGEXPLORER_H_ */
