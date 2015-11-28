#include <iostream>
#include "Utils.h"

typedef struct {
	int a;
	double b;
	char c;
	long long l;
	void *p;
} type_t;

class A {
public:
	type_t t;
};

class B {
public:
	B() : t() {}
	type_t t;
};

int main()
{
	type_t t1;
	std::cout << "struct NOT initalized to 0" << std::endl;
	Utils::printHex(&t1, sizeof(type_t));

	type_t t2 = {};
	std::cout << "struct initalized to 0" << std::endl;
	Utils::printHex(&t2, sizeof(type_t));

	type_t *t3 = new type_t;
	std::cout << "struct NOT initalized to 0" << std::endl;
	Utils::printHex(t3, sizeof(type_t));
	//  extended initializer lists only available with -std=c++11 or -std=gnu++11 [enabled by default]
	(*t3) = {};
	std::cout << "struct initalized to 0" << std::endl;
	Utils::printHex(t3, sizeof(type_t));

	A a;
	std::cout << "struct NOT initalized to 0" << std::endl;
	Utils::printHex(&a.t, sizeof(type_t));

	B b;
	std::cout << "struct initalized to 0" << std::endl;
	Utils::printHex(&b.t, sizeof(type_t));
}
