package org.mahu.proto.cglibtest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class FuncCgLib {

	private final static Logger LOGGER = Logger.getLogger(FuncCgLib.class
			.getName());
	
	private static Class superClass = BaseClass.class;

	public static <T> BaseClass<T> nop(final Class<T> clz) {
		Enhancer enhancer = new Enhancer();
		//enhancer.setNamingPolicy(new MyNamingPolicy(clz));
		enhancer.setSuperclass(superClass);
		BaseClassInterceptor interceptor = new BaseClassInterceptor(clz);
		enhancer.setCallback(interceptor);
		Class<?> paramType = java.lang.Class.class;
		@SuppressWarnings("unchecked")
		BaseClass<T> proxy = (BaseClass<T>) enhancer.create(
				new Class[] { paramType }, new Object[] { clz });
		return proxy;
	}

	protected static class BaseClassInterceptor implements MethodInterceptor {
		private Object proxy;
		private final Class<?> clazz = BaseClass.class;

		public BaseClassInterceptor(final Class<?> clz) {
			try {
				this.proxy = clazz.getConstructor(Class.class).newInstance(clz);
				LOGGER.info("Created proxy of instance " + clazz.getName() + " with parameterType: "+clz.getName());
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		public Object intercept(final Object o, final Method m,
				final Object[] args, MethodProxy mp) throws Throwable {
			LOGGER.finer("intercept m : " + m);
			Method binding = findMethodInProxy(m);
			binding.setAccessible(true);
			Object ob = binding.invoke(proxy, args);
			return ob;
		}

		protected Method findMethodInProxy(final Method m)
				throws NoSuchMethodException {
			Method binding = null;
			Method[] meths = clazz.getMethods();
			for (Method mi : meths) {
				if (mi.equals(m)) {
					binding = mi;
					break;
				}
			}
			if (binding == null)
				throw new NoSuchMethodException(m.toString());
			return binding;
		}
	}

	static class MyNamingPolicy implements NamingPolicy {
		private final Class<?> parameterType;

		MyNamingPolicy(final Class<?> parameterType) {
			this.parameterType = parameterType;
			LOGGER.info("MyNamingPolicy with parameterType: "+parameterType.getName());
		}

		@Override
		public String getClassName(java.lang.String prefix,
				java.lang.String source, java.lang.Object key, Predicate names) {		
			String className = prefix + parameterType.getSimpleName();
			LOGGER.info("getClassName with prefix: "+prefix+ " parameterType.getSimpleName(): "+parameterType.getSimpleName() + " className: "+className);				
			return className;
		}
	}
}
