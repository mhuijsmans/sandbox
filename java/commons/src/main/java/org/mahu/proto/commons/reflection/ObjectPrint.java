package org.mahu.proto.commons.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

public class ObjectPrint {

	public ObjectPrint(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public String print(Object a_object) {
		java.io.StringWriter sw = new java.io.StringWriter(8 * 2048);
		this.os = new java.io.PrintWriter(((java.io.Writer) (sw)));
		this.list = new java.util.LinkedList();
		if (a_object == null) {
			print("[NULL]");
		} else {
			print(a_object.getClass().getName());
			print(a_object, 0);
		}
		os.flush();
		return sw.toString();
	}

	void print(Object a_object, int a_depth) {
		if (isAlreadyPrinted(a_object)) {
			return;
		}
		if (maxDepth > 0 && a_depth > maxDepth) {
			return;
		}

		Class[] classes = ObjectInfo.getClassHierarchy(a_object);
		String spaces = "";
		for (int i = classes.length - 1; i >= 0; i--) {
			spaces = spaces + " ";
			String className = spaces + _splitRight(classes[i].getName(), ".");
			Field[] fields = classes[i].getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				// Only print the non-static variables
				if (ObjectInfo.isFieldStatic(fields[j])) {
					continue;
				}
				ObjectInfo.setAccessible(fields[j]);
				String fieldName = className + "." + fields[j].getName();
				try {
					Object object = fields[j].get(a_object);
					if (object != null && object.getClass().isArray()) {
						int len = Array.getLength(object);
						for (int k = 0; k < len; k++) {
							Object element = Array.get(object, k);
							Class arrClass = element == null ? null : element
									.getClass();
							print(element, arrClass, fieldName, a_depth + 1, k);
						}
					} else // NULL -or- No Array
					{
						print(object, fields[j].getType(), fieldName,
								a_depth + 1, -1);
					}
				} catch (Exception e) {
					print(e, a_object);
				}
			}
		}
	}

	void print(Object a_object, Class a_Class, String a_fieldName, int a_depth,
			int a_arrayElement) {
		String fieldName = (a_arrayElement > -1) ? a_fieldName + "  "
				+ a_arrayElement : a_fieldName;
		if (a_object != null) {
			print(a_depth, fieldName, "");
			Class clazz = a_object.getClass();
			if (clazz == Integer.class || clazz == Long.class
					|| clazz == Float.class || clazz == Boolean.class
					|| clazz == Double.class || clazz == Byte.class
					|| clazz == Character.class || clazz == Short.class
					|| clazz == String.class || clazz == Date.class) {
				print(a_depth + 1, a_object.getClass().getName(),
						a_object.toString());
			} else if (printMap(a_object, a_depth)) {
				;
			} else { // It not a basic datatype, so it a Object
				print(a_object, a_depth);
			}
		} else {
			print(a_depth, fieldName, "[NULL]");
		}
	}

	private boolean printMap(Object a_object, int a_depth) {
		if (a_object instanceof Map) {
			for (Object key : ((Map) a_object).keySet()) {
				Object value = ((Map) a_object).get(key);
				print(a_depth + 1, "key", key.toString());
				print(a_depth + 1, "value", value.toString());
			}
			return true;
		}
		return false;
	}

	public void print(String a_msg) {
		os.println(a_msg);
		// System.out.println(a_msg);
	}

	public void print(int indent, String a_property, String a_value) {
		for (int i = 0; i < indent; i++) {
			a_property = "  " + a_property;
		}
		print(a_property, a_value);
	}

	public void print(String a_property, String a_value) {
		if (a_value == null) {
			a_value = "[NULL]";
		}
		print(a_property + ": " + a_value);
	}

	public void print(boolean a_true) {
		if (a_true) {
			print("true");
		} else {
			print("false");
		}
	}

	public void print(byte[] bytes) {
		print(new String(bytes));
	}

	public void print(Integer i) {
		print(i.toString());
	}

	public void print(int i) {
		print(new Integer(i));
	}

	public void print(Long l) {
		print(l.toString());
	}

	public void print(long l) {
		print(new Long(l));
	}

	public void print(Double d) {
		print(d.toString());
	}

	public void print(double d) {
		print(new Double(d));
	}

	public void print(Exception e, Object in) {
		String l_msg;
		if (e != null) {
			l_msg = "!!! " + e.getClass().getName();
			String l_exMsg = e.getMessage();
			if (l_exMsg != null) {
				l_msg += " " + l_exMsg;
			}
		} else {
			l_msg = "!!!Exception print";
		}
		if (in != null) {
			l_msg += " in " + in.getClass().getName();
		}
		print(l_msg);
		if (e != null) {
			e.printStackTrace(os);
		}
	}

	public String _splitRight(String i_String, String a_Splitter) {
		String l_Return = "";
		int l_pos = i_String.lastIndexOf(a_Splitter);
		if (l_pos > 0) {
			l_Return = i_String.substring(l_pos + a_Splitter.length());
			i_String = i_String.substring(0, l_pos);
		}
		return l_Return;
	}

	private final boolean isAlreadyPrinted(Object o) {
		final boolean b = list.contains(o);
		if (!b) {
			list.add(o);
		}
		return b;
	}

	// class data
	java.io.PrintWriter os; // Just a buffer
	java.util.LinkedList list = null;
	int maxDepth;
}
