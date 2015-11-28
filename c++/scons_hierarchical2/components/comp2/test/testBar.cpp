/*
 * Filename: testBar.cpp
 */

#include "gtest/gtest.h"
#include "Bar.h"

TEST(FooTest, test_expect) {
	Bar bar;
	EXPECT_EQ(1, bar.One());
}


