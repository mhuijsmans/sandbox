package org.mahu.proto.xsdcodegenerate;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MyClass extends MyBase {
	private final Class<?> cls;
	// private final Map<String, MyMethod> methods = new HashMap<String,
	// MyMethod>();
	private final Map<String, MyField> fields = new HashMap<String, MyField>();

	MyClass(final Class<?> cls) {
		this.cls = cls;
		if (DEBUG) {
			println("Adding class: " + cls);
		}
	}

	public String getName() {
		return cls.getName();
	}

	public String getSimpleName() {
		return cls.getSimpleName();
	}

	public void addField(final Field f) {
		MyField myField = new MyField(f);
		fields.put(myField.getNameXmlElement(), myField);
	}

	public Collection<MyField> getFields() {
		return fields.values();
	}

	// public void addMethod(final Method m) {
	// String parameterName = getParameterName(m);
	// MyMethod myMethod = methods.get(parameterName);
	// if (myMethod == null) {
	// myMethod = new MyMethod(parameterName, m);
	// methods.put(parameterName, myMethod);
	// } else {
	// myMethod.addMethod(m);
	// }
	// }

	// public void check() {
	// for (MyMethod m : methods.values()) {
	// m.check();
	// }
	// }

	// private String getParameterName(final Method m) {
	// String name = m.getName();
	// assertTrue(name.length() >= 4,
	// "Name expected to be of length >=4, found " + name);
	// return name.substring(3);
	// }

	// public Collection<MyMethod> getMethods() {
	// return methods.values();
	// }
}