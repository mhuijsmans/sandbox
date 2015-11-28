/*
 * Filename: Foo.h
 */

#ifndef FOO_H_
#define FOO_H_

class Foo  {
public:
	Foo();
	virtual ~Foo();

	virtual int One();
	virtual int Two() const;
    virtual int OneArgument(int);
};

#endif /* FOO_H_ */
