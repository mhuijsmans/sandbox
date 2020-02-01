package org.mahu.guicetest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

// This test case aims to find all bound interfaces
public class GuiceBindingIterator {

	static class BindingModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(Data1.class);
			// Data 2 (default constructor) is not bound, but can be injected
			// Data 3 (@Inject constructor) is not bound, but is/can be injected
			// Data 4 (@Inject members) is not bound, but is/can be injected
			bind(TestObject.class);
			bind(IData5.class).to(Data5.class);
			bind(IData6.class).to(Data6.class);
			bind(Data7.class);
			bind(IData8.class).to(Data8.class);
		}
	}

	static class Data1 {
	}

	static class Data2 {
	}

	static class Data3 {
		@Inject
		Data3(Data1 data1, IData6 data6) {

		}
	}

	static class Data4 {
		@Inject
		Data1 data1;
	}

	static interface IData5 {
	}

	// The interface shall be implemented by a class; It shall not be used
	static interface ILifeCycleInterface {
		void init();

		void destroy();
	}

	@ILifeCyclePhase(phase=LifeCyclePhase.PHASE0)
	static class Data5 implements IData5, ILifeCycleInterface {

		@Inject
		Data5(IData6 data6) {

		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
		}

		@Override
		public void destroy() {

		}
	}

	static interface IData6 {
	}

	static class Data6 implements IData6 {
	}

	@ILifeCyclePhase(phase=LifeCyclePhase.PHASE0)	
	static class Data7 implements ILifeCycleInterface {

		@Inject
		Data7(IData6 data6) {
		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
		}

		@Override
		public void destroy() {

		}
	}
	
	static interface IData8 extends ILifeCycleInterface {
	}
	
	static class Data8 implements IData8 {

		@Inject
		Data8(IData6 data6) {

		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
		}

		@Override
		public void destroy() {

		}
	}
	
	enum LifeCyclePhase {
		PHASE0, // reading of settings, no external dependency
		        // starting threadpool
		PHASE1, // starting services, using settings/services from phase0. 
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ILifeCyclePhase {
		LifeCyclePhase phase() default LifeCyclePhase.PHASE1;
	}	

	static class TestObject {

		@Inject
		TestObject(Data1 data1, Data1 data2, Data3 data3, Data4 data4, IData5 data5a, Data5 data5b) {
		}
	}

	@Test
	public void testBasicBinding() throws Exception {
		BindingModule bindingModule = new BindingModule();
		Injector injector = Guice.createInjector(bindingModule);
		printBindings(injector);
		callInit(injector);
	}

	private static void printBindings(final Injector injector) {
		System.out.println("========================================================================");
		System.out.println("Nr of bindings=" + injector.getAllBindings().keySet().size());
		// getAllBindings() will returns all bindings.
		// for bind(someClass1); and bind(someInterface2).to(someClass2);
		// It will return someClass1, someClass2, someInterface2
		for (Key<?> key : injector.getAllBindings().keySet()) {
			Class<?> clazz = key.getTypeLiteral().getRawType();
			System.out.println("Binding.rawtype=" + clazz.getName());
		}
	}

	private static void callInit(final Injector injector) {
		System.out.println("========================================================================");
		for (Key<?> key : injector.getAllBindings().keySet()) {
			final Class<?> clazz = key.getTypeLiteral().getRawType();
			if (ILifeCycleInterface.class.isAssignableFrom(clazz)) {
				System.out.println("Binding.rawtype=" + clazz.getName() + " implements ILifeCycleInterface");
				if (clazz.isInterface()) {
					System.out.println("  Error, interface is not allowed to implement ILifeCycleInterface");
				} else {
					final ILifeCyclePhase lifeCyclePhase = clazz.getDeclaredAnnotation(ILifeCyclePhase.class);
					if (lifeCyclePhase != null) {
						try {
							System.out.println("  lifeCyclePhase="+lifeCyclePhase.phase());
						} catch (IllegalAccessError e) {
							// I get an exception with above code. Unclear how to fix it.
							// java.lang.IllegalAccessError: tried to access class org.mahu.guicetest.GuiceBindingIterator$LifeCyclePhase from class com.sun.proxy.$Proxy8
							// at com.sun.proxy.$Proxy8.phase(Unknown Source)
							// at org.mahu.guicetest.GuiceBindingIterator.callInit(GuiceBindingIterator.java:175)
							//
							// Workaround is to create ILifeCycleInterfacePhase0, ILifeCycleInterfacePhase1
							// when phase0 is invoked before phase 1.
							//
							// Other workaround is to add a parameter to init: init(LifeCyclePhase).
						}
					}
					ILifeCycleInterface yourInterface = (ILifeCycleInterface) injector.getInstance(key);
					yourInterface.init();
				}
			}
		}
	}

}
