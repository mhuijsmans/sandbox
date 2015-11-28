/*
 * Filename: testFoo.cpp
 */

#include <stdexcept>
#include <stdio.h>
#include "gtest/gtest.h"

#include "Foo.h"

// ref: https://code.google.com/p/googletest/wiki/V1_7_Primer
// ref: https://code.google.com/p/googletest/wiki/V1_7_AdvancedGuide

TEST(FooTest, test_expect) {
	Foo foo;
	EXPECT_EQ(0, foo.One());
	std::cout << "Expect is not fatal, so execution continues" << std::endl;	
}

TEST(FooTest, test_assert) {
	Foo foo;
	ASSERT_EQ(0, foo.One());
	std::cout << "Assert is fatal, so execution is aborted" << std::endl;
}

// For debugging exceptions, set the GTEST_CATCH_EXCEPTIONS environment variable to 0, 
// or use the --gtest_catch_exceptions=0 flag when running the tests.
// See section: Disabling Catching Test-Thrown Exceptions
// in 
TEST(FooTest, test_at_exception_gtest_continues) {
	throw std::runtime_error("gtest catches exceptions and continues");
}

TEST(FooTest, test_exception_caught_by_testcase) {
	Foo foo;
	ASSERT_THROW(foo.ThrowException(), std::runtime_error);
	ASSERT_ANY_THROW(foo.ThrowException());
	ASSERT_NO_THROW(foo.One());
}

TEST(FooTest, test_fail) {
	FAIL();
}

TEST(FooTest, test_succeed) {
	SUCCEED();
}

// Death test is explained here
TEST(FooDeathTest, deathByInvalidInput) {
	std::cout << "FooDeathTest::deathByInvalidInput, pid=" << getpid() << std::endl;
	// Nothing happens on valid argument
	ASSERT_EQ(0, Foo::ExitOnNegativeArgument(0));
	// See: https://code.google.com/p/googletest/wiki/AdvancedGuide#Death_Test_Styles
	// section: Death Test Styles
	// I used this example: http://www.ibm.com/developerworks/aix/library/au-googletestingframework.html
	// Also interesting is this: http://stackoverflow.com/questions/14062628/how-to-make-google-test-detect-the-number-of-threads-on-linux
	// Without next line, deathTest fail on linux.
	::testing::FLAGS_gtest_death_test_style = "threadsafe";
	// Note: the 3rd arg ("..") is the output from std::cerr
	EXPECT_EXIT(Foo::ExitOnNegativeArgument(-1), ::testing::ExitedWithCode(1), "Invalid argument: -1");
}

void subRoutineTest() {
	Foo foo;
	ASSERT_EQ(0, foo.One());
}

TEST(FooTest, testUsingSubRoutines) {
	{
		// Text that will be added when test case fails.
		SCOPED_TRACE("Text added when test fails");
		subRoutineTest();
		std::cout << "Execution continues here" << std::endl;
	}
	std::cout << "Testing for fatal failures" << std::endl;
	if (HasFatalFailure()) {
		std::cout << "There are fatal failures, test case can terminate. But here it continues." << std::endl;
    }
	ASSERT_NO_FATAL_FAILURE( subRoutineTest() );
	std::cout << "Execution does not reach this point" << std::endl;
}

TEST(FooTest, testFooData) {
	Foo foo;
	int one = foo.One();
	ASSERT_EQ(1, one);
	std::cout << "In gtest 1.7.0 only string and int can be logged" << std::endl;
	RecordProperty("Foo.One()", one);
}



