#include <iostream>
#include <ostream>
#include <typeinfo>

#include "SrcLocation.h"

namespace arrow {

class Helper {
public:
	void hello();
};

}

void arrow::Helper::hello() {
	std::cout << "Where: " << CLS_SRC_LOCATION << std::endl;
}

int main() {
	std::cout << "Hello" << std::endl;
	
	std::cout << "Where: " << SRC_LOCATION << std::endl;
	arrow::Helper h;
	h.hello();
}
