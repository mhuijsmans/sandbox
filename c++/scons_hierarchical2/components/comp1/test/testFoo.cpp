/*
 * Filename: testFoo.cpp
 */

#include "gtest/gtest.h"

#include "Foo.h"

// ref: https://code.google.com/p/googletest/wiki/V1_7_Primer
// ref: https://code.google.com/p/googletest/wiki/V1_7_AdvancedGuide

TEST(FooTest, test_expect) {
	Foo foo;
	EXPECT_EQ(1, foo.One());
}


