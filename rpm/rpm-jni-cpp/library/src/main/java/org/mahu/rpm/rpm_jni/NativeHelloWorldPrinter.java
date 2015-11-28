package org.mahu.rpm.rpm_jni;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mahu.rpm.rpm_jni.api.HelloWorldPrinter;

public class NativeHelloWorldPrinter implements HelloWorldPrinter {

	static {
		try {
			// call static method: void NarSystem.loadLibrary();
			// Reflection is used because NarSystem is generated code.
			String packageName = NativeHelloWorldPrinter.class.getPackage().getName();
			Class<?> cls = Class.forName(packageName+".NarSystem");
			Class<?>[] methodArgClasses = null;
			Method method = cls.getMethod("loadLibrary", methodArgClasses);
			Object[]invokeArgs = null;
			method.invoke(null, invokeArgs);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Failed to invoke: NarSystem.loadLibrary()", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
					"Failed to invoke: NarSystem.loadLibrary()", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Failed to invoke: NarSystem.loadLibrary()", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(
					"Failed to invoke: NarSystem.loadLibrary()", e);
		} catch (SecurityException e) {
			throw new RuntimeException(
					"Failed to invoke: NarSystem.loadLibrary()", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Failed to invoke: NarSystem.loadLibrary()", e);
		}
	}

	/**
	 * Print hello world to standard out
	 */
	public native void printHelloWorld();

}
