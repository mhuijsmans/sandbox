package org.mahu.proto.test;

import com.google.inject.Injector;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		AppModule1 appModule1 = new AppModule1();
		AppModule2 appModule2 = new AppModule2();

		// By creating your Injector this way various special annotations
		// (@PostConstruct, etc.) will be processed.
		Injector injector = LifecycleInjector.builder().withModules(appModule1, appModule2).build().createInjector();

		// Starting the Governator LifecycleManager causes field validation to be
		// processed and the Governator @WarmUp methods to get executed.
		LifecycleManager manager = injector.getInstance(LifecycleManager.class);
		// The manager MUST be started. Note: this method waits indefinitely for warm up
		// methods to complete
		manager.start();

		// When your application is shutting down, stop the LifecycleManager so that
		// @PreDestroy methods are executed.
		manager.close();
	}
}