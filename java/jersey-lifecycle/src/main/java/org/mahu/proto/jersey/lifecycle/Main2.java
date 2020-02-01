package org.mahu.proto.jersey.lifecycle;

import javax.inject.Singleton;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

public class Main2 implements IMain {

	private final Injector injector;
	
	Main2() {
		injector = createInjector();
	}

	@Override
	public void init() {
		injector.getInstance(ILifeCycleInterface.class).init();
	}

	@Override
	public void destroy() {
		injector.getInstance(ILifeCycleInterface.class).init();
	}

	private static Injector createInjector() {
		Injector injector = Guice.createInjector(new MyModule());
		return injector;
	}
	
	// One approach: discover all interfaces that implements the LifeCycleInterface;
	// That would allow to invoke many lifecycle based classes. 
	// Decide to drop that approach, 'the many' felt uncontrolled
	private static void allBindings(final Injector injector) {
		System.out.println("Nr of bindings="+injector.getAllBindings().keySet().size());
		for (Key<?> key : injector.getAllBindings().keySet()) {
			System.out.println("Binding.rawtype="+key.getTypeLiteral().getRawType().getName());
			Class<?> clazz = key.getTypeLiteral().getRawType();
			// for: bind(ISettingsReader.class).to(SettingsReader.class).in(Singleton.class);
			// Guice will return both ISettingsReader and SettingsReader. Therefore chweck on interface.
			if (clazz.isInterface() &&  ILifeCycleInterface.class.isAssignableFrom(clazz)) {
				ILifeCycleInterface yourInterface = (ILifeCycleInterface) injector.getInstance(key);
				yourInterface.init();
			}
		}
	}	

}
