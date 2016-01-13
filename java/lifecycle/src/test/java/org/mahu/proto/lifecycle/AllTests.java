package org.mahu.proto.lifecycle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses({
  APIBrokerTest.class,
  EventBusServiceTest.class,
  GuaveEventBusTest.class,
  GuiceTest.class,
  InitDestroyTest.class,
  ReadyAbortLockTest.class,
  ModuleBindings2Test.class,
  ObjectRegistryTest.class,
  PublicServiceKeyTest.class,
  RequestProxyDispatchServiceTest.class,
  ServiceLifeCycleManagerTest.class
})
public class AllTests {
}
//@formatter:on