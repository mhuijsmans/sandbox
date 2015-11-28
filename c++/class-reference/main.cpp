#include <iomanip>
#include <iostream>
#include <memory>
#include <ostream>
#include <sstream>
#include <string>

namespace abc
{
namespace statebraffel
{
class StateNorch {
public:
	StateNorch() {}
};
}
}

// short hand notation for namespace;
namespace sb = ::abc::statebraffel;

namespace {
// anonymous namespace is great for testing
}

// ref: http://en.cppreference.com/w/cpp/language/type_alias
// type alias, identical to
// typedef void (*func)(int, int);
using func = void (*) (int,int);

// final can be set on class
class Reference final
{
public:
	// A "= default" can be set, e.g. on default constructor, to
	// tell that default implementation shall be generated.
	Reference()
	{
		// do not throw exception in constructor / destructor
	}
	// virtual destructor mandatory when class has virtual methods
	virtual ~Reference()
	{
	}

	/**
	 *  Documentation for a method
	 *  @param name text ..
	 *  @return returns ...
	 *  @throws <name exception>
	 */
	// Use const for arguments
	// use const for methods, if it doesn't change state.
	// do not use for method: throws, noexcep
	void Method1(const int x, const std::string &s) const
	{
	}

	// When possible use const for exposed data structures
	std::shared_ptr<const std::string> GetSomeObject() {
		std::shared_ptr<const std::string> object;
		return object;
	}

	/**
	 * use of shared_ptr or reference needs to be analysed case by case.
	 * - if static structure, reference is a good choice
	 * - if data is passed through the system, and it has a own life cycle,
	 *   shared_ptr is a better choice.
	 */
	void Method2(std::shared_ptr<const std::string> obj1, const std::string &obj2) {
	}

private:
	// usage of these methods is not allowed
	Reference(const Reference& that) = delete;
	Reference& operator=(Reference const&) = delete;
};

// ********************************
// strongly type enum (since c++)
// a ref: http://www.cprogramming.com/c++11/c++11-nullptr-strongly-typed-enum-class.html
enum class NameEnum
{
	VALUE1, VALUE2
};
// Extend ostream to support printing to stream
std::ostream& operator<<(std::ostream& os, const NameEnum enumType)
{
	switch (enumType)
	{
	case NameEnum::VALUE1:
		os << "VALUE1";
		break;
	}
}

// *********************************

void print(uint8_t b)
{
	// std::cout will treat b as character, which may contain control characters.
	// But b shall be treated as unsigned integer, so copy.
	uint32_t t = b;
	std::stringstream os;
	os << std::hex << std::setfill('0') << std::setw(2) << t;
	std::cout << os.str();
}

// *********************************

union example_t
{
	uint32_t uint32;
	struct
	{
		uint8_t b0;
		uint8_t b1;
		uint8_t b2;
		uint8_t b3;
	};
	struct
	{
		unsigned int p0 :10;
		unsigned int p1 :10;
		unsigned int p2 :10;
		unsigned int p3 :2;
	};
	uint8_t b[4];
} example;

// *********************************

int main(int argc, char **argv)
{

	std::cout << "main ENTER" << std::endl;

	// In C++11, unique can handle array, but shared requires array destructor
	// Is that true? Test it.
	std::unique_ptr<int> unique_good(new int[10]);
	std::shared_ptr<int> shared_good(new int[10], std::default_delete<int[]>());

	// Casting unsigned char to char: This all DOES not work/compile
	//  std::shared_ptr<const uint8_t> mask;
	//  std::shared_ptr<const char> tmp1 = std::static_pointer_cast< const char >( mask );
  //  std::shared_ptr<const char> tmp2 = std::dynamic_pointer_cast< const char >( mask );
  //  std::shared_ptr<const char> tmp3 = std::const_pointer_cast< const char >( mask );
  //  std::shared_ptr<const char> tmp4 = reinterpret_cast< std::shared_ptr<const char> >( mask );

	// to test / assign pointer C++11 has defined nullptr

	std::cout << "main LEAVE" << std::endl;

	return 0;
}
