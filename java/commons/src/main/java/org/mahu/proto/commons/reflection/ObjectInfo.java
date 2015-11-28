package org.mahu.proto.commons.reflection;

import java.lang.reflect.Field;
import java.util.LinkedList;

final public class ObjectInfo {

	public final static void print(Object o) {
		// System.out.println("***ObjectInfo***");
		print(o.getClass());
	}

	public final static void print(Class c) {
		System.out.println("***ClassInfo***");
		while (c != null) {
			System.out.println("- " + c.getName());
			java.lang.reflect.Method[] methods = c.getDeclaredMethods();
			for (int j = 0; j < methods.length; j++)
				System.out.println("--- " + methods[j].toString());
			c = c.getSuperclass();
		}
		System.out.println("***");
	}

	public final static String getAttrValue(Object o, String name) {
		String s = null;
		try {
			Class c = o.getClass();
			java.lang.reflect.Field field = null;
			while (c != null && field == null) {
				try {
					field = c.getDeclaredField(name);
				} catch (java.lang.NoSuchFieldException e) {
					c = c.getSuperclass();
				}
			}
			// getField only works for public fields
			// java.lang.reflect.Field field = c.getField(name);
			field.setAccessible(true);
			s = field.get(o).toString();
		} catch (Exception e) {
			throw new AssertionError(e);
		}
		return s;
	}

	public static LinkedList getObjectAttributes(Object o) {
		LinkedList list = new LinkedList();
		Class[] classes = ObjectInfo.getClassHierarchy(o);
		for (int i = classes.length - 1; i >= 0; i--) {
			Field[] fields = classes[i].getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				// Only print the non-static variables
				Field field = fields[j];
				if (isFieldStatic(field))
					continue;
				setAccessible(field);
				list.add(new ObjectField(o, field));
			}
		}
		return list;
	}

	public final static Class[] getClassHierarchy(Object a_object) {
		if (a_object == null)
			return new Class[0];
		Class[] classes = new Class[getClassHierarchyDepth(a_object.getClass())];
		classes[0] = a_object.getClass();
		return getClassHierarchy(classes, 0);
	}

	public final static Class[] getClassHierarchy(Class[] a_classes, int i) {
		Class clazz = a_classes[i].getSuperclass();
		if (clazz == null || clazz.equals(Object.class)) {
			return a_classes;
		}
		i++;
		a_classes[i] = clazz;
		return getClassHierarchy(a_classes, i);
	}

	public final static int getClassHierarchyDepth(Class baseClass) {
		Class clazz = baseClass.getSuperclass();
		if (clazz == null)
			return 1;
		return (clazz.equals(Object.class)) ? 1
				: getClassHierarchyDepth(clazz) + 1;
	}

	public static final void setAccessible(Field a_accessible) {
		a_accessible.setAccessible(true);
	}

	public static boolean isFieldStatic(Field field) {
		final int mod = field.getModifiers();
		return java.lang.reflect.Modifier.isStatic(mod);
	}

}
