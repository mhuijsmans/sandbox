#include <iostream>
#include <thread>         // std::this_thread::sleep_for
#include <chrono>         // std::chrono::seconds

class mystring {
public:
	mystring(std::string &v_) : v(v_), id(cntr++) { std::cout << "mystring.ctor()-" << id << " " << v <<std::endl;}
	mystring(const char *v_) : v(v_), id(cntr++) { std::cout << "mystring.ctor()-" << id << " " << v <<std::endl;}
	~mystring() { std::cout << "mystring.dtor()-" << id << " " << v <<std::endl;}

	friend std::ostream& operator<< (std::ostream& stream, const mystring& me) {
		stream << "mystring-" << me.id << " " << me.v;
	}
private: 
	std::string v;
	int id;
	static int cntr;
};
int mystring::cntr=0;

// found at: http://stackoverflow.com/questions/10379691/creating-macro-using-line-for-different-variable-names
// which also explain the layering
#define CONCATENATE_DIRECT(s1, s2) s1##s2
#define CONCATENATE(s1, s2) CONCATENATE_DIRECT(s1, s2)
#define ANONYMOUS_VARIABLE(str) CONCATENATE(str, __LINE__)
#define LOGGER static const mystring ANONYMOUS_VARIABLE(var) (__PRETTY_FUNCTION__); std::cout << ANONYMOUS_VARIABLE(var)

class Helper {
public:
	static void callStatic() { 
		std::cout << "Helper::callStatic ENTER" << std::endl;
		// class m1 is created only once, even when callStatic is called multiple times
		static const mystring m1("class.staticMethod.static.const");
		// class m2 is created everytime callStatic is called
		const mystring m2("class.staticMethod.const");
		std::cout << "Helper::callStatic LEAVE" << std::endl;
	}
};

namespace test1 {
	class Buddy {
	public:
		void callNonStatic() { 
			std::cout << "Buddy.callNonStatic ENTER" << std::endl;
			const mystring m2("class.staticMethod.const");
			std::cout << "Buddy.callNonStatic LEAVE" << std::endl;
		}	
	};
}

namespace test2 {
	class Buddy {
	public:
		static void callStatic() { 
		// std:cout is usd to object creation/deletion is visible
			std::cout << "Buddy.callNonStatic ENTER" << std::endl;
			LOGGER << " HI-1" << std::endl;
			LOGGER << " HI-2" << std::endl;
			std::cout << "Buddy.callNonStatic LEAVE" << std::endl;
		}		
		void callNonStatic() { 
			std::cout << "Buddy.callNonStatic ENTER" << std::endl;
			LOGGER << " HI-1" << std::endl;
			std::cout << "Buddy.callNonStatic LEAVE" << std::endl;
		}	
	};
}

// ==================== the tests code ========================

static mystring m1("global.static");
static const mystring m2("global.static.const");

int main(int argc, char **argv) {

	std::cout << "*****************************************************************" << std::endl;

	std::cout << "main ENTER" << std::endl;

	// Observation (from gcc -E): 
	// __LINE__ is replaved by preprocessor, but __PRETTY_FUNCTION__ is not
	// so __PRETTY_FUNCTION__ is a keyword recognize by the gcc compiler
	std::cout << __LINE__ << " " << __PRETTY_FUNCTION__ << std::endl;

	LOGGER << "Message1" <<std::endl;
	LOGGER << "Message2" <<std::endl;

	static mystring m5("main.static");
	static const mystring m6("main.static.const");

	std::cout << "===============================================" << std::endl;

	Helper::callStatic();
	Helper::callStatic();

	std::cout << "===============================================" << std::endl;
	{
		test1::Buddy b;
		b.callNonStatic();
		b.callNonStatic();	
	}
	std::cout << "===============================================" << std::endl;

	{
		// Manual inspection should show that the class holding the class-method name 
		// is created only one
		test2::Buddy b;
		b.callNonStatic();
		b.callNonStatic();	
		test2::Buddy::callStatic();
		test2::Buddy::callStatic();
	}

	std::cout << "===============================================" << std::endl;	

	std::cout << "main LEAVE" << std::endl;
	return 0;
}


