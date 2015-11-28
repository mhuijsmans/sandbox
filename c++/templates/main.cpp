#include <iostream>

// A_Type represent a datatype.
template<class A_Type> class calc {
public:
	A_Type multiply(A_Type x, A_Type y);
	A_Type add(A_Type x, A_Type y);
};

template<class A_Type> A_Type calc<A_Type>::multiply(A_Type x, A_Type y) {
	return x * y;
}

template<class A_Type> A_Type calc<A_Type>::add(A_Type x, A_Type y) {
	return x + y;
}

// ---------------------------

void hello(const char *str) {
	std::cout << str << std::endl;
}

int main() {
	std::cout << "hi" << std::endl;

	calc<double> doubleCalc;
	std::cout << "Calculation: " << doubleCalc.add(1, 2) << " "
			<< doubleCalc.multiply(1, 2) << std::endl;

	hello("martien");
}
