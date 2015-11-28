package org.mahu.proto.xsdcodegenerate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public	class MyClassMngr extends MyBase {
	Map<String, MyClass> classes = new HashMap<String, MyClass>();

	public MyClass add(Class<?> cls) {
		assertTrue(cls != null, "Null method not allowed");
		String className = cls.getName();
		MyClass myClass = new MyClass(cls);
		classes.put(className, myClass);
		return myClass;
	}

	public MyClass get(final Class<?> cls) {
		String className = cls.getName();
		return classes.get(className);
	}

	public boolean exists(final Class<?> cls) {
		assertTrue(cls != null, "Null method not allowed");
		String className = cls.getName();
		return classes.containsKey(className);
	}

//	public void checkClasses() {
//		for (MyClass mc : classes.values()) {
//			mc.check();
//		}
//	}

	public Collection<MyClass> getClasses() {
		return classes.values();
	}
	
}