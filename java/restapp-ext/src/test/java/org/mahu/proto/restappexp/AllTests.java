package org.mahu.proto.restappexp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IntegrationTest.class, IntegrationErrorTest.class,
		EventBusTest.class, ProtocolLoaderServiceTest.class, BpmnTest.class,
		ResourceManagerTest.class, IntegrationMultiSystemTest.class })
public class AllTests {
}
