/*
 * Filename: testBar.cpp
 */

#include "gtest/gtest.h"
#include "Mildrid.h"

TEST(FooTest, test_expect) {
	Mildrid mildrid;
	EXPECT_EQ(1, mildrid.One());
}


