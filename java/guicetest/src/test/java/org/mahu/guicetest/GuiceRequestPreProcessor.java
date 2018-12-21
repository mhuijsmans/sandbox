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
 * 
 */
public class GuiceRequestPreProcessor {

	private static final String NOARG_RESPONSE = "name";

	public enum TAG {
		ONE, TWO
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RequestProperties {
		Class<? extends AbstractModule> module();

		TAG tag();
	}

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

	static class MyGlobalModule extends AbstractModule {
		@Override
		protected void configure() {
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

	static interface InstanceProvider {
		Object getInstance();
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

	public interface IRequestPreProcessor {

		public <T> T with(AbstractModule childModule, Class<T> requestInterface);

		public <T> T with(Class<T> requestInterface);

		public <T> T with(final RequestId<T> requestId);
	}

	static class Handler implements InvocationHandler {

		private final InstanceProvider instanceProvider;
		private final RequestPreProcessor requestPreProcessor;

		Handler(final InstanceProvider instanceProvider, final RequestPreProcessor requestPreProcessor) {
			this.instanceProvider = instanceProvider;
			this.requestPreProcessor = requestPreProcessor;
		}

		public Object invoke(Object proxy, Method method, Object[] arguments)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			System.out.println(" Handler, method=" + method.getName());
			return requestPreProcessor.execute(instanceProvider, method, arguments);
		}
	}

	static class RequestPreProcessor implements IRequestPreProcessor {

		private final Injector injector;
		private String blo = null;
		private RuntimeException e = null;

		RequestPreProcessor(final Injector injector) {
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
		public <T> T with(Class<T> requestInterface) {
			InstanceProvider instanceProvider = () -> {
				System.out.println(" with(only interface)");
				return getInstance(injector, requestInterface);
			};

			Handler handler = new Handler(instanceProvider, this);

			@SuppressWarnings("unchecked")
			T f = (T) Proxy.newProxyInstance(requestInterface.getClassLoader(), new Class[] { requestInterface },
					handler);
			return f;
		}

		@Override
		public <T> T with(RequestId<T> requestId) {
			return with(requestId.clazz);
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

		private static Object getInstance(final Injector injector, Class<?> interfaceClass) {
			RequestProperties requestProperties = interfaceClass.getAnnotation(RequestProperties.class);
			if (requestProperties != null) {
				try {
					AbstractModule childModule = requestProperties.module().newInstance();
					return injector.createChildInjector(childModule).getInstance(interfaceClass);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			} else {
				return injector.getInstance(interfaceClass);
			}
		}

		private static Object getInstance(final Injector injector, final AbstractModule childModule,
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
	public void dynamicProxy_interfaceWithModuleWithArguments_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(new MyRequestWithArgModule(), IRequestWithArguments.class)
				.executeWithString(dynamicData);

		assertEquals(dynamicData, response);
	}

	@Test
	public void dynamicProxy_requestIdWithArguments_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(RequestId.ONE).executeWithString(dynamicData);

		assertEquals(dynamicData, response);
	}

	@Test
	public void dynamicProxy_interfaceWithAnnotatedModuleWithArguments_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(IRequestWithArguments.class).executeWithString(dynamicData);

		assertEquals(dynamicData, response);
	}

	@Test
	public void dynamicProxy_noArgument_correctResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);

		String response = requestPreProcessor.with(new MyRequestWithArgModule(), IRequestWithoutArguments.class)
				.executeNoArg();

		assertEquals(NOARG_RESPONSE, response);
	}

	@Test
	public void dynamicProxy_noArgumentNoModule_correctResponse() {
		Injector injector = Guice.createInjector(new MyGlobalModule());
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);

		String response = requestPreProcessor.with(IRequestWithoutArguments.class).executeNoArg();

		assertEquals(NOARG_RESPONSE, response);
	}

	@Test
	public void dynamicProxy_otherResponse() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);
		String blo = "blo";
		requestPreProcessor.setResponse(blo);

		String dynamicData = "bla";
		String response = requestPreProcessor.with(IRequestWithArguments.class).executeWithString(dynamicData);

		assertEquals(blo, response);
	}

	@Test
	public void dynamicProxy_exception() {
		Injector injector = Guice.createInjector();
		RequestPreProcessor requestPreProcessor = new RequestPreProcessor(injector);
		String help = "help";
		RuntimeException ee = new RuntimeException(help);
		requestPreProcessor.setException(ee);

		String dynamicData = "bla";
		try {
			requestPreProcessor.with(IRequestWithArguments.class).executeWithString(dynamicData);
			fail("Exception thrown");
		} catch (RuntimeException e) {
			assertEquals(ee, e);
			assertEquals(help, e.getMessage());
		}
	}

}
