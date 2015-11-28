package org.mahu.proto.forkprocesstest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PrintSystemEnvironmentAndPropertiesTest.class,
		CloneOwnProcessTest.class, StartOSChildProcessTest.class, JunitChildForkTest.class })
public class AllTests {

}
