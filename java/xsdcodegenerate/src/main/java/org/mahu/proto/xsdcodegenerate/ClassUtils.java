package org.mahu.proto.xsdcodegenerate;

import java.lang.reflect.Field;

public class ClassUtils extends MyBase {
	private final MyClassMngr mgr;

	ClassUtils(final MyClassMngr mgr) {
		this.mgr = mgr;
	}

	public MyClass inspectClass(final Class<?> cls) {
		if (cls.isPrimitive()) {
			return null;
		}
		if (mgr.exists(cls)) {
			return null;
		}
		if (DEBUG) {
			println("********************************************************");
		}
		MyClass myClass = mgr.add(cls);
		for (Field f : cls.getDeclaredFields()) {
			myClass.addField(f);
		}
		for (MyField f : myClass.getFields()) {
			inspectClass(f.getFieldClass());
		}
		return myClass;
	}

}
