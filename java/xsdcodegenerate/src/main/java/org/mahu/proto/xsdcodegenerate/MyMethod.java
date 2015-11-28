package org.mahu.proto.xsdcodegenerate;

import java.lang.reflect.Method;

public class MyMethod extends MyBase {
	private String parameterName;
	private Method getMethod = null;
	private Method setMethod = null;

	MyMethod(final String parameterName, final Method m) {
		addMethod(m);
		this.parameterName = parameterName;
		if (DEBUG) {
			println("Adding parameterName: " + parameterName);
		}
	}

	public void addMethod(final Method m) {
		String methodName = m.getName();
		assertTrue(methodName.startsWith(GET) || methodName.startsWith(SET),
				"Not supported method: " + methodName);
		if (methodName.startsWith(SET)) {
			setMethod = m;
		}
		if (methodName.startsWith(GET)) {
			getMethod = m;
		}
	}

	public void check() {
		assertTrue(getMethod != null, "Get method missing for " + parameterName);
		assertTrue(setMethod != null, "Set method missing for " + parameterName);
	}

	String getParameterName() {
		return parameterName;
	}

	Method getGetMethod() {
		return getMethod;
	}

	String getReturnTypeGetMethodFirstLetterUppercase() {
		String returnType = getMethod.getGenericReturnType().toString();
		returnType = returnType.substring(0, 1).toUpperCase()
				+ returnType.substring(1);
		assertSupportedReturnType(returnType);
		return returnType;
	}

	Method getSetMethod() {
		return setMethod;
	}

	void assertSupportedReturnType(final String t) {
		assertTrue(t.equals("Int") || t.equals("Double"),
				"Only Int and Double are supported, found: " + t);
	}

}