package org.mahu.proto.jnitest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ JniTest.class, JniError1Test.class, JniError2Test.class,
		SpecialCasesTest.class, LoggingTest.class })
public class AllTests {

}