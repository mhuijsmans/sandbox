package org.mahu.proto.jerseyjunittools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ HelloWorldTest.class, MyProviderTest.class })
public class AllTests {
}
