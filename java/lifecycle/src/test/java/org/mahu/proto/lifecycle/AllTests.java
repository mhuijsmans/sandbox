package org.mahu.proto.lifecycle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses({
  APIBrokerTest.class,
  EventBusServiceTest.class,
  ExecutorServiceTest.class,
  ExecutorUtilsTest.class,
  GuaveEventBusTest.class,
  GuiceTest.class,
  InitDestroyTest.class,
  LifeCycleManagerTest.class,
  ModuleBindings2Test.class,
  ObjectRegistryTest.class,
  PublicServiceKeyTest.class,
  ReadyAbortLockTest.class,  
  RequestProxyDispatchServiceTest.class,
  ServiceLifeCycleControlTest.class,
  StopEventTest.class,
  ThreadFactoryFactoryTest.class
})
public class AllTests {
}
//@formatter:on