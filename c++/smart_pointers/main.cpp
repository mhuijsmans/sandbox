#include <cstdint>
#include <iostream>
#include <memory>

namespace MySpace {

class MyClass {
public:
	MyClass() {
		std::cout << "MyClass.ctor()" << std::endl;
	}

	typedef ::std::shared_ptr<::MySpace::MyClass> MyClassSharedPtr;
	typedef ::std::shared_ptr<::MySpace::MyClass> MyClassUniquePtr;
};

typedef ::std::shared_ptr<::MySpace::MyClass> MyClassSharedPtr;

class MyOtherClass {
public:
	MyOtherClass(::MySpace::MyClassSharedPtr ptr) :
			myClassSharedPtr(ptr) {
		std::cout << "MyOtherClass.ctor()" << std::endl;
	}

	::MySpace::MyClass::MyClassSharedPtr myClassSharedPtr;
};

}

std::unique_ptr<::MySpace::MyClass> PassUniquePointer(std::unique_ptr<::MySpace::MyClass> up)
{
	std::cout << "PassUniquePointer: up.holds_object=" << (up.get()!=nullptr) << std::endl;
	// Next line is possible. This seems only situation where unique pointer can be passed.
	return up;
}

int main() {
	//
	{
		::MySpace::MyClass::MyClassSharedPtr aPtr(new ::MySpace::MyClass());
		::MySpace::MyOtherClass myOtherClass(aPtr);
	}
	//
	{
		::MySpace::MyClassSharedPtr aPtr(new ::MySpace::MyClass());
		::MySpace::MyOtherClass myOtherClass(aPtr);
	}
	//
	{
		::MySpace::MyOtherClass myOtherClass(::MySpace::MyClassSharedPtr(new ::MySpace::MyClass()));
	}
	{
		 ::MySpace::MyOtherClass myOtherClass(std::make_shared<MySpace::MyClass>());
	}
	{
		auto sp = std::make_shared<MySpace::MyClass>();
		::MySpace::MyOtherClass myOtherClass(sp);
	}

	{
		std::shared_ptr<uint8_t> ptr1(new uint8_t[16], std::default_delete<uint8_t[]>());
		std::shared_ptr<uint16_t> ptr2(new uint16_t[16], std::default_delete<uint16_t[]>());

		// Next lines try to do casting with basic data types.
		// They result in compiler error: target is not pointer or reference to class
//		std::shared_ptr<uint8_t> ptr2a;
//		ptr2a = std::dynamic_pointer_cast<uint8_t>(ptr2);

		// Next lines result in compiler error: source is not a pointer to a class.
//		std::shared_ptr<void> ptr2b;
//		ptr2b = std::dynamic_pointer_cast<void>(ptr2);
	}

	// Ntex blocks explores moving unique_ptr's around and from unique to shared_ptr.
	{
		std::unique_ptr<::MySpace::MyClass> up1(new ::MySpace::MyClass());
		std::cout << "up1.holds_object=" << (up1.get()!=nullptr) << std::endl;
		std::unique_ptr<::MySpace::MyClass> up2;
		// assignment operator doesn't work
		// up2 = up1;
		up2 = std::move(up1);
		std::cout << "up1.holds_object=" << (up1.get()!=nullptr) << std::endl;
		std::cout << "up2.holds_object=" << (up2.get()!=nullptr) << std::endl;
		// Next line doesn't work. Move shall be used.
		// PassUniquePointer(up2);
		up1 = std::move( PassUniquePointer(std::move(up2)) );
		std::cout << "up2.holds_object=" << (up2.get()!=nullptr) << std::endl;
		std::cout << "up1.holds_object=" << (up1.get()!=nullptr) << std::endl;

		std::shared_ptr<::MySpace::MyClass> sp;
		std::cout << "sp.useCount=" << sp.use_count() << std::endl;
		sp = std::move(up1);
		std::cout << "up1.holds_object=" << (up1.get()!=nullptr) << std::endl;
		std::cout << "sp.useCount=" << sp.use_count() << std::endl;
	}

}
