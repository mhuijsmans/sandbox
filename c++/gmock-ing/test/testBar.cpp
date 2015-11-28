/*
 * Filename: testFoo.cpp
 */

// gmock cheatsheet: https://code.google.com/p/googlemock/wiki/CheatSheet

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using ::testing::_;
using ::testing::Invoke;
using ::testing::AtLeast;
using ::testing::InSequence;
using ::testing::Return;
using ::testing::InvokeWithoutArgs;
using ::testing::WithArgs;

#include <iostream>

#include "Bar.h"
#include "Foo.h"

// This class mocks all virtual methods
class MockFoo1 : public Foo {
public:
	MOCK_METHOD0(One, int());
	// two() is const
	MOCK_CONST_METHOD0(Two, int());
};

// This class mocks one virtual methods. Other virtual methods are still called.
class MockFoo2 : public Foo {
public:
	MOCK_METHOD0(One, int());

	int OneFromBase() { return Foo::One(); }
};

// This class mocks one virtual methods. Other virtual methods are still called.
class MockFoo3 : public Foo {
public:
    MOCK_METHOD1(OneArgument, int(int));

    int OneArgumentFromBase(int a) { return Foo::OneArgument(a); }
};

// This class mocks one virtual methods with default in constructor.
// Other virtual methods are still called.
class MockFoo4 : public Foo {
public:
    MockFoo4() {
        EXPECT_CALL(*this, OneArgument(_)).
            WillRepeatedly(Return(15));
    }
    MOCK_METHOD1(OneArgument, int(int));
};

// recommendations
// TEST(<classname>Test, <methodName>_<event>_<expected-outcome>)
// Mock is called: <classname>Mock

TEST(BarTest, noMock) {
	std::cout << "*** BarTest::noMock" << std::endl;
	Foo foo;
	Bar bar(foo);
	EXPECT_EQ(1, bar.doOne());
	EXPECT_EQ(2, bar.doTwo());
}

TEST(BarTest, mockAll_and_CheckedTheyAreCalled) {
    std::cout << "*** BarTest::mockAll_and_CheckedTheyAreCalled" << std::endl;
    MockFoo1 foo;
    // Next line states that One() is called exactly once. GMock will check that.
    EXPECT_CALL(foo, One()).WillOnce(Return(1));
    EXPECT_CALL(foo, Two()).WillOnce(Return(2));

    Bar bar(foo);
    EXPECT_EQ(1, bar.doOne());
    EXPECT_EQ(2, bar.doTwo());
}

TEST(BarTest, mockAll2_and_CheckedTheyAreCalled) {
    std::cout << "*** BarTest::mockAll_and_CheckedTheyAreCalled" << std::endl;
    MockFoo1 foo;
    // Next lines states that One() is called 2 times. GMock will check that.
    EXPECT_CALL(foo, One()).
        WillOnce(Return(1)).
        WillOnce(Return(1));
    EXPECT_CALL(foo, Two()).WillOnce(Return(2));

    Bar bar(foo);
    EXPECT_EQ(1, bar.doOne());
    EXPECT_EQ(1, bar.doOne());
    EXPECT_EQ(2, bar.doTwo());
}

TEST(BarTest, mockInSequence) {
    std::cout << "*** BarTest::mockInSequence" << std::endl;
    MockFoo1 foo;
    // Sequence glues the expect_calls together.
    {
        InSequence s;
        EXPECT_CALL(foo, One()).
            Times(2).
            WillRepeatedly(Return(4));
        EXPECT_CALL(foo, One()).
            WillOnce(Return(5));
    }

    Bar bar(foo);
    EXPECT_EQ(4, bar.doOne());
    EXPECT_EQ(4, bar.doOne());
    EXPECT_EQ(5, bar.doOne());
}

TEST(BarTest, mockOne_and_CheckedItIsCalled) {
	std::cout << "*** BarTest::mockOne_and_CheckedItIsCalled" << std::endl;
	MockFoo2 foo;
	// The mocked method One() will call OneFromBase() in mocked class.
	EXPECT_CALL(foo, One()).WillOnce(InvokeWithoutArgs(&foo, &MockFoo2::OneFromBase));

	Bar bar(foo);
	EXPECT_EQ(1, bar.doOne());
	EXPECT_EQ(2, bar.doTwo());
}

TEST(BarTest, mockOneArgument_and_CheckedItIsCalled) {
    std::cout << "*** BarTest::mockOne_and_CheckedItIsCalled" << std::endl;
    MockFoo3 foo;
    // The mocked method OneArgument() will call OneArgumentFromBase() in mocked class.
    EXPECT_CALL(foo, OneArgument(_)).
        WillOnce(WithArgs<0>( Invoke(&foo, &MockFoo3::OneArgumentFromBase) ));

    Bar bar(foo);
    EXPECT_EQ(1, bar.doOne());
    EXPECT_EQ(2, bar.doTwo());
    EXPECT_EQ(3, bar.OneArgument(3));
    // uncommenting next line will make test case fail, because mock is called twice
    // EXPECT_EQ(3, bar.OneArgument(3));
}

TEST(BarTest, mockOneArgumentSeveralCalls_and_CheckedItIsCalled) {
    std::cout << "*** BarTest::mockOneArgumentSeveralCalls_and_CheckedItIsCalled" << std::endl;
    MockFoo3 foo;
    // The mocked method OneArgument() will call OneArgumentFromBase() in mocked class.
    EXPECT_CALL(foo, OneArgument(_)).
        Times(2). // To be called exactly 2 times
        WillOnce(WithArgs<0>( Invoke(&foo, &MockFoo3::OneArgumentFromBase) )).
        WillOnce( Return(10) );

    Bar bar(foo);
    EXPECT_EQ(1, bar.doOne());
    EXPECT_EQ(2, bar.doTwo());
    EXPECT_EQ(3, bar.OneArgument(3));
    EXPECT_EQ(10, bar.OneArgument(5));
}

TEST(BarTest, mockOneArgumentDefaultInConstructor_and_CheckedItIsCalled) {
    std::cout << "*** BarTest::mockOneArgumentDefaultInConstructor_and_CheckedItIsCalled" << std::endl;
    MockFoo4 foo;
    // No EXPECT_CALL, so the settings from constructor is used
    Bar bar(foo);
    EXPECT_EQ(1, bar.doOne());
    EXPECT_EQ(2, bar.doTwo());
    EXPECT_EQ(15, bar.OneArgument(3));
    EXPECT_EQ(15, bar.OneArgument(5));
}





