package org.mahu.proto.xsdcodegenerate;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;

public class MyField extends MyBase {
	private final Field field;
	private Class<?> fieldClass;
	private XmlElement fieldXmlElement;

	MyField(final Field field) {
		this.field = field;
		fieldClass = field.getType();
		fieldXmlElement = field.getAnnotation(XmlElement.class);
		assertTrue(fieldXmlElement != null,
				"Field doesn't have XMLElement annotation " + field.getName());
		if (DEBUG) {
			println("New field: %s of class: %s", getNameXmlElement(),
					getFieldClassName());
		}
	}
	
	public Field getField() {
		return field;
	}

	public String getNameXmlElement() {
		return fieldXmlElement.name();
	}

	public Class<?> getTypeXmlElement() {
		return fieldXmlElement.type();
	}

	public final String getFieldClassName() {
		return fieldClass.getName();
	}

	public final Class<?> getFieldClass() {
		return fieldClass;
	}

	public final String getFieldSimpleClassName() {
		return fieldClass.getSimpleName();
	}

	public boolean isPrimitive() {
		return fieldClass.isPrimitive();
	}

	public String getGetMethod() {
		return "get" + getNameXmlElement();
	}

	public String getFieldObjectTypeFirstLetterUppercase() {
		String returnType = fieldClass.toString();
		returnType = returnType.substring(0, 1).toUpperCase()
				+ returnType.substring(1);
		assertSupportedReturnType(returnType);
		return returnType;
	}

	public void assertSupportedReturnType(final String t) {
		assertTrue(t.equals("Int") || t.equals("Double"),
				"Only Int and Double are supported, found: " + t);
	}
}
