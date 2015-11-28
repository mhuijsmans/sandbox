#include <iostream>

class A {
public:
	A() {
		std::cout << "A.ctor()" << std::endl;
	}
};

void sayHello() {
	std::cout << "sayHello()" << std::endl;
}

namespace SpaceA {
class A {
public:
	A() {
		std::cout << "SpaceA::A.ctor()" << std::endl;
	}
};
void sayHello() {
	std::cout << "SpaceA::sayHello()" << std::endl;
}
}

int main() {
	std::cout << "SpaceA.hello" << std::endl;
	A a1;
	SpaceA::A a2;
	sayHello();
	SpaceA::sayHello();

	// I think next line should work,  but it does not.
	// I found similar example: http://www.cplusplus.com/doc/tutorial/namespaces/
	//using namespace SpaceA;
	//A a3;
}
