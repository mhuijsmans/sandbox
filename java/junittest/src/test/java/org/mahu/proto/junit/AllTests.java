package org.mahu.proto.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ExpectedExceptionRuleTest.class, FixedOrderTest.class,
		MyTestRuleTest.class, ParameterizedTest.class,
		TemporaryFolderRuleTest.class, Test1.class, Test2.class,
		TestNameRuleTest.class, TestWatcherRuleTest.class,
		TimeoutRuleTest.class })
public class AllTests {

}