/*
 * Filename: Foo.h
 */

#ifndef FOO_H_
#define FOO_H_

class Foo {
public:
	Foo();
	virtual ~Foo();
	
	int One() { return 1; }
	
	void ThrowException();
	static int ExitOnNegativeArgument(int v);
private:	
	int Two() { return 2; }
};

#endif /* FOO_H_ */
