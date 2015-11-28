#include "gtest/gtest.h"

int main(int argc, char** argv) {
	::testing::InitGoogleTest(&argc, argv);
	// fast generates an error on linux. 
	// but threadsafe doesn't for valgrind
	// threadsafe works on linux; fast is alternative
	::testing::FLAGS_gtest_death_test_style = "fast";
	return RUN_ALL_TESTS();
}

