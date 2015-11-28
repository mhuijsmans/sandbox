package org.mahu.proto.commons.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.util.Date;

/**
 * ObjectField holds Object and field inside the object
 */
public class ObjectField {

  ObjectField(Object o, Field field) {
    this.field = field;
    this.object = o;
    ObjectInfo.setAccessible(field);
  }

  /*
   * Two ObjectFields are the same if their Fields are the same
   * and their values are the same.
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof ObjectField)) {
      return false;
    }
    ObjectField that = (ObjectField) obj;
    // Field equals: declared by the same class and have the same name and type.
    if (!field.equals(that.field)) {
      return false;
    }
    try {
      Object val1 = this.field.get(this.object);
      Object val2 = that.field.get(that.object);
      return equals(val1, val2);
    } catch (Exception e) {
    	throw new AssertionError(e);
    }
  }

  public String toString() {
    return field.toString();
  }

  private final boolean equals(Object val1, Object val2) {
    if (val1 == null && val2 == null) {
      return true;
    }
    if (val1 == null || val2 == null) {
      return false;
    }
    if (val1.getClass() != val2.getClass()) {
    	throw new AssertionError("Class mismatch");
    }
    Class clazz1 = val1.getClass();
    if (clazz1 == Integer.class ||
      clazz1 == Long.class ||
      clazz1 == Float.class ||
      clazz1 == Boolean.class ||
      clazz1 == Double.class ||
      clazz1 == Byte.class ||
      clazz1 == Character.class ||
      clazz1 == Short.class ||
      clazz1 == String.class ||
      clazz1 == Date.class) {
      return val1.toString().equals(val2.toString());
    }
    if (clazz1.isArray()) {
      int len1 = Array.getLength(val1);
      int len2 = Array.getLength(val2);
      // do not compare contents
      return len1 == len2;
    }
    // Anything else, do not compare
    return true;
  }

// class data
  private Field field;
  private Object object;
}

