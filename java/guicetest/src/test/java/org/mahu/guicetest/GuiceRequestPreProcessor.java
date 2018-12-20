package org.mahu.guicetest;

import static org.junit.Assert.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 2 implementations for dynamic

 */
public class GuiceRequestPreProcessor {

	private static final String NOARG_RESPONSE = "name";

	public enum TAG {
		ONE, TWO
	}

	static class EmptyModule extends AbstractModule {

		@Override
		protected void configure() {
			// empty
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RequestProperties {
		Class<? extends AbstractModule> module() default EmptyModule.class;

		TAG tag();
	}

	// with annotation ?
	// Annotation: binding Module?
	static class MyRequestWithArg implements IRequestWithArguments {

		@Override
		public String executeWithString(String name) {
			return name;
		}

	}

	@RequestProperties(module = MyRequestWithArgModule.class, tag = TAG.ONE)
	interface IRequestWithArguments {
		String executeWithString(String dynamicData);
	}

	static class MyRequestWithArgModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(IRequestWithArguments.class).to(MyRequestWithArg.class);
			bind(IRequestWithoutArguments.class).to(MyRequestWitNoArg.class);
		}
	}

	static class MyRequestWitNoArg implements IRequestWithoutArguments {

		@Override
		public String executeNoArg() {
			return NOARG_RESPONSE;
		}

	}

	interface IRequestWithoutArguments {
		String executeNoArg();
	}

	interface IRequest {
	}

	abstract class IRequestModule<T> extends AbstractModule {

	}

	public interface IRequestPreProcessor {

		/**
		 * Resolve the requestInterface via the provide module and execute it with the
		 * provided arguments. The requestInterface shall have a single method with the
		 * provided arguments.
		 * 
		 * @param childModule
		 * @param requestInterface
		 * @param arguments
		 * @return
		 */
		String execute(AbstractModule childModule, Class<?> requestInterface, Object... arguments);

		String execute(Class<?> requestInterface, Object... arguments);
	}

	static interface InstanceProvider {
		Object getInstance();
	}

	static class RequestPreProcessor implements IRequestPreProcessor {

		private final Injector injector;
		private String blo = null;
		private RuntimeException e = null;

		RequestPreProcessor(final Injector injector) {
			this.injector = injector;
		}

		@Override
		public String execute(Class<?> requestInterface, Object... arguments) {
			InstanceProvider instanceProvider = () -> injector.getInstance(requestInterface);
			return execute(instanceProvider, requestInterface, arguments);
		}

		@Override
		public String execute(AbstractModule childModule, Class<?> requestInterface, Object... arguments) {
			InstanceProvider instanceProvider = () -> {
				System.out.println(" getInstance(injector, childModule, requestInterface)");
				return getInstance(injector, childModule, requestInterface);
			};
			return execute(instanceProvider, requestInterface, arguments);
		}

		private String execute(InstanceProvider instanceProvider, Class<?> requestInterface, Object... arguments) {
			return callStatemachine(() -> {
				final Object obj = instanceProvider.getInstance();
				final Class<?>[] argClasses = new Class<?>[arguments.length];
				for (int i = 0; i < arguments.length; i++) {
					argClasses[i] = arguments[i].getClass();
				}
				try {
					final Method method = requestInterface.getMethods()[0];
					System.out.println("  execute(..) method=" + method.toString());
					return (String) method.invoke(obj, arguments);
				} catch (SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			});
		}

		public static Object getInstance(final Injector injector, final AbstractModule childModule,
				Class<?> interfaceClass) {
			return injector.createChildInjector(childModule).getInstance(interfaceClass);
		}

		private String callStatemachine(final IRequestWithoutArguments request) {
			System.out.println("callStatemachine(..)");
			if (blo == null && e == null) {
				return request.executeNoArg();
			} else {
				if (e == null) {
					return blo;
				} else {
					throw e;
				}
			}
		}

	}
	

	@Test
	public void execute_methodWithArguments() {
		Injector injector = Guice.createInjector();
		String data = "hi";
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);
		String response = requestPreProcessor.execute(new MyRequestWithArgModule(), IRequestWithArguments.class, data);

		assertEquals(data, response);
	}

	@Test
	public void execute_methodWithoutArguments() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);
		String response = requestPreProcessor.execute(new MyRequestWithArgModule(), IRequestWithoutArguments.class);

		assertEquals(NOARG_RESPONSE, response);
	}

	@Test
	public void executeMethodWithArguments_argumentMissing_exception() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);
		try {
			requestPreProcessor.execute(new MyRequestWithArgModule(), IRequestWithArguments.class);
			fail("Argument missing, so exception expected");
		} catch (RuntimeException e) {
			//
		}
	}

	@Test
	public void executeMethodWithArguments_tooManyArguments_exception() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);
		try {
			String data = "hi";
			requestPreProcessor.execute(new MyRequestWithArgModule(), IRequestWithArguments.class, data, data);
			fail("Argument missing, so exception expected");
		} catch (RuntimeException e) {
			//
		}
	}
	
	// ==================================================
	// ============= using dynamic proxy ================
	// ==================================================
	
	static class RequestId<T> {
		
		private final Class<T> clazz;
		
		public static final RequestId<IRequestWithArguments> ONE = new RequestId<>(IRequestWithArguments.class);
		
		private RequestId(Class<T> clazz) {
			this.clazz = clazz;
		}

		public Class<T> getClazz() {
			return clazz;
		}
		
	}
	
	public interface IRequestPreProcessor2 {

		public <T> T with(AbstractModule childModule, Class<T> requestInterface);
		
		public <T> T with(AbstractModule childModule, final RequestId<T> requestId);
	}	
	
	static class Handler implements InvocationHandler {

		private final InstanceProvider instanceProvider;
		private final RequestPreProcessor2 requestPreProcessor;

		Handler(final InstanceProvider instanceProvider, final RequestPreProcessor2 requestPreProcessor) {
			this.instanceProvider = instanceProvider;
			this.requestPreProcessor = requestPreProcessor;
		}

		public Object invoke(Object proxy, Method method, Object[] arguments)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			System.out.println(" Handler, method=" + method.getName());
			return requestPreProcessor.execute(instanceProvider, method, arguments);
		}
	}

	static class RequestPreProcessor2 implements IRequestPreProcessor2 {

		private final Injector injector;
		private String blo = null;
		private RuntimeException e = null;

		RequestPreProcessor2(final Injector injector) {
			this.injector = injector;
		}

		@Override
		public <T> T with(AbstractModule childModule, Class<T> requestInterface) {
			InstanceProvider instanceProvider = () -> {
				System.out.println(" with(..)");
				return getInstance(injector, childModule, requestInterface);
			};

			Handler handler = new Handler(instanceProvider, this);

			@SuppressWarnings("unchecked")
			T f = (T) Proxy.newProxyInstance(requestInterface.getClassLoader(), new Class[] { requestInterface },
					handler);
			return f;
		}
		
		@Override
		public <T> T with(AbstractModule childModule, RequestId<T> requestId) {
			return with(childModule, requestId.clazz);
		}		

		private String execute(InstanceProvider instanceProvider, Method method, Object... arguments) {
			return callStatemachine(() -> {
				final Object obj = instanceProvider.getInstance();
				System.out.println("  execute(..) method=" + method.toString());
				try {
					return (String) method.invoke(obj, arguments);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					System.out.println("  execute(..) caugth exception");
					throw new RuntimeException(e);
				}
			});
		}

		public static Object getInstance(final Injector injector, final AbstractModule childModule,
				Class<?> interfaceClass) {
			return injector.createChildInjector(childModule).getInstance(interfaceClass);
		}

		private String callStatemachine(final IRequestWithoutArguments request) {
			System.out.println("callStatemachine(..)");
			if (blo == null && e == null) {
				return request.executeNoArg();
			} else {
				if (e == null) {
					return blo;
				} else {
					throw e;
				}
			}
		}

		public void setResponse(String blo) {
			this.blo = blo;
		}

		public void setException(RuntimeException e) {
			this.e = e;
		}

	}
	
	@Test
	public void dynamicProxy_interfaceWithArgument_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor2 requestPreProcessor = new RequestPreProcessor2(injector);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(new MyRequestWithArgModule(), IRequestWithArguments.class)
				.executeWithString(dynamicData);

		assertEquals(dynamicData, response);
	}
	
	@Test
	public void dynamicProxy_requestIdWithArgument_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor2 requestPreProcessor = new RequestPreProcessor2(injector);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(new MyRequestWithArgModule(), RequestId.ONE)
				.executeWithString(dynamicData);

		assertEquals(dynamicData, response);
	}	
	
	@Test
	public void dynamicProxy_noArgument_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor2 requestPreProcessor = new RequestPreProcessor2(injector);

		String response = requestPreProcessor.with(new MyRequestWithArgModule(), IRequestWithoutArguments.class)
				.executeNoArg();

		assertEquals(NOARG_RESPONSE, response);
	}	

	@Test
	public void dynamicProxy_otherResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor2 requestPreProcessor = new RequestPreProcessor2(injector);
		String blo = "blo";
		requestPreProcessor.setResponse(blo);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(new MyRequestWithArgModule(), IRequestWithArguments.class)
				.executeWithString(dynamicData);

		assertEquals(blo, response);
	}

	@Test
	public void dynamicProxy_exception() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor2 requestPreProcessor = new RequestPreProcessor2(injector);
		String help = "help";
		RuntimeException ee = new RuntimeException(help);
		requestPreProcessor.setException(ee);

		String dynamicData = "bla";
		try {
			requestPreProcessor.with(new MyRequestWithArgModule(), IRequestWithArguments.class)
					.executeWithString(dynamicData);
			fail("Exception thrown");
		} catch (RuntimeException e) {
			assertEquals(ee, e);
			assertEquals(help, e.getMessage());
		}
	}

}
