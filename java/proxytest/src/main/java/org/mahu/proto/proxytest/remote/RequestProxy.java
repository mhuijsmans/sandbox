package org.mahu.proto.proxytest.remote;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

public class RequestProxy implements InvocationHandler {
	private static final Logger log = Logger.getLogger(RequestProxy.class
			.getName());

	private final Class<?> cls;
	private RequestExecutor requestExecutor;

	public RequestProxy(final Class<?> cls, RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
		this.cls = cls;
		checkClassIsPublic();
	}

	// @Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// Note: using proxy in next log statement, causes stackOverflow,
		// because
		// it results in another invoke(..)
		log.fine("invoke proxy: " + " method: " + method + " args");
		if (Object.class == method.getDeclaringClass()) {
			String name = method.getName();
			if ("equals".equals(name)) {
				return proxy == args[0];
			} else if ("hashCode".equals(name)) {
				return System.identityHashCode(proxy);
			} else if ("toString".equals(name)) {
				return proxy.getClass().getName() + "@"
						+ Integer.toHexString(System.identityHashCode(proxy))
						+ ", with InvocationHandler " + this;
			} else {
				throw new IllegalStateException(String.valueOf(method));
			}
		}
		MyInterface request = new MyInterface(cls, method, args);
		return requestExecutor.executeTask(request);
	}
	
	private void checkClassIsPublic() {
		int modifiers = cls.getModifiers();
		checkArgument(Modifier.isPublic(modifiers));
	}	

	static class MyInterface implements RequestExecutor.Request<Object>,
			Serializable {
		private static final long serialVersionUID = 6362695764104189627L;
		private final String className;
		private final String methodName;
		private final String[] parameterTypes;
		private final Object[] args;

		public MyInterface(final Class<?> cls, final Method method,
				final Object[] args) {
			this.className = cls.getName();
			this.methodName = method.getName();
			this.parameterTypes = createParameterTypes(method
					.getParameterTypes());
			this.args = args;
		}

		@Override
		public Object execute(final Class<?>[] registeredClasses)
				throws RemoteException {
			log.fine("Reached the other side.");
			Class<?> requiredInterface = getInterfaceClass();
			Class<?> matchingClass = findMatchingClass(registeredClasses,
					requiredInterface);
			Method method = getMethodInClass(matchingClass);
			Object instance = newInstance(matchingClass);
			return invokeMethod(method, instance);
		}
		
		public String toString() {
			return MyInterface.class.getSimpleName() + " cls=" + className
					+ " method=" + methodName;
		}		

		private Object invokeMethod(Method method, Object instance)
				throws RemoteException {
			try {
				Object obj =  method.invoke(instance, args);
				log.fine("Invoked method: "+method);				
				return obj;
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RemoteException(
						"Invoked failed on for object of class: "
								+ instance.getClass().getName()
								+ " for method: " + method, e);
			}
		}

		private Object newInstance(Class<?> matchingClass) throws RemoteException {
			try {
				Object obj =  matchingClass.newInstance();
				log.fine("Created new instance of : "+matchingClass.getName());
				return obj;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RemoteException("NewInstance failed for class: "
						+ matchingClass, e);
			}
		}

		private Method getMethodInClass(Class<?> matchingClass) throws RemoteException {
			Class<?>[] parameterTypes = createParameterTypes();
			try {
				Method method = matchingClass.getDeclaredMethod(methodName, parameterTypes);
				log.fine("Found method : "+method);
				return method;
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RemoteException(
						"Location method failed for class: "
								+ matchingClass.getName()
								+ " for method: " + methodName, e);
			}
		}

		private Class<?> getInterfaceClass() throws RemoteException {
			Class<?> requiredInterface;
			try {
				requiredInterface = Class.forName(className);
				log.fine("Found interface class: "+requiredInterface.getName());
			} catch (ClassNotFoundException e) {
				throw new RemoteException("Requested interface not found: "
						+ className);
			}
			return requiredInterface;
		}

		private Class<?> findMatchingClass(
				final Class<?>[] registeredClasses, Class<?> requiredInterface)
				throws RemoteException {
			Class<?> matchingClass = null;
			for (Class<?> cls : registeredClasses) {
				if (requiredInterface.isAssignableFrom(cls)) {
					log.fine("Found matching class: " + cls.getName());
					matchingClass = cls;
				}
			}
			if (matchingClass == null) {
				throw new RemoteException(
						"No class implements the requested interface: "
								+ className);
			}
			return matchingClass;
		}

		private String[] createParameterTypes(Class<?>[] parameters) {
			String[] parametersClassNames = new String[parameters.length];
			int i = 0;
			for (Class<?> par : parameters) {
				parametersClassNames[i++] = par.getName();
			}
			return parametersClassNames;
		}

		private Class<?>[] createParameterTypes() throws RemoteException {
			String lastParametersClassName = null;
			try {
				Class<?>[] parameters = new Class<?>[parameterTypes.length];
				int i = 0;
				for (String parametersClassName : parameterTypes) {
					lastParametersClassName = parametersClassName;
					parameters[i++] = Class.forName(parametersClassName);
				}
				return parameters;
			} catch (ClassNotFoundException e) {
				throw new RemoteException("Unable to find parameter class: "
						+ lastParametersClassName);
			}
		}
	}
}
