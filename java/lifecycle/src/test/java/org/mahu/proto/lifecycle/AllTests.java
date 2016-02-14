package org.mahu.proto.lifecycle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mahu.proto.lifecycle.impl.APIBrokerTest;
import org.mahu.proto.lifecycle.impl.ExecutorUtilsTest;
import org.mahu.proto.lifecycle.impl.LifeCycleManagerTest;
import org.mahu.proto.lifecycle.impl.LifeCycleTaskExceptionTest;
import org.mahu.proto.lifecycle.impl.ObjectRegistryTest;
import org.mahu.proto.lifecycle.impl.PublicServiceKeyTest;
import org.mahu.proto.lifecycle.impl.ReadyAbortLockTest;
import org.mahu.proto.lifecycle.impl.RequestProxyDispatchServiceTest;
import org.mahu.proto.lifecycle.impl.ServiceLifeCycleControlTest;
import org.mahu.proto.lifecycle.impl.ServicesLifeCycleControlUncaughtExceptionHandlerTest;
import org.mahu.proto.lifecycle.impl.ThreadFactoryFactoryTest;
import org.mahu.proto.lifecycle.impl.UncaughtExceptionTaskTest;

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses({
  APIBrokerTest.class,
  EventBusServiceTest.class,
  ExecutorServiceTest.class,
  ExecutorUtilsTest.class,
  LifeCycleTaskExceptionTest.class,
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
  ServicesLifeCycleControlUncaughtExceptionHandlerTest.class,
  StopEventTest.class,
  ThreadFactoryFactoryTest.class,
  UncaughtExceptionTaskTest.class
})
public class AllTests {
}
//@formatter:on