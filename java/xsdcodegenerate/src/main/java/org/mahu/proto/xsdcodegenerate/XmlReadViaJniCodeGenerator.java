package org.mahu.proto.xsdcodegenerate;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class XmlReadViaJniCodeGenerator extends MyBase {

	private final MyClassMngr mgr;

	class BasicDatatype {
		final Class<?> cls;
		final String basictype;

		BasicDatatype(final Class<?> cls, final String basictype) {
			this.cls = cls;
			this.basictype = basictype;
		}
	}

	// List is incomplete
	private BasicDatatype[] primitiveDataTypes = new BasicDatatype[] {
			new BasicDatatype(Integer.class, "int"),
			new BasicDatatype(Double.class, "double") };

	XmlReadViaJniCodeGenerator(final MyClassMngr mgr) {
		this.mgr = mgr;
	}

	public void generateCodeForReadingOfXmlObjects(final String structName,
			final String jObjectRef, final MyClass myRootClass) {
		generateCodeForXmlObject();
		generateReadCodeForClassAndChildren(structName, jObjectRef, myRootClass);
	}

	private void generateCodeForXmlObject() {
		for (MyClass cls : mgr.getClasses()) {
			if (!cls.getName().startsWith("java."))
				println("const XmlObject cls%s(\"%s\");", cls.getSimpleName(),
						cls.getName().replace('.', '/'));
		}
	}

	private void generateReadCodeForClassAndChildren(final String structName,
			final String jObjectRef, final MyClass rootCls) {
		final String jObjectName = "j" + rootCls.getSimpleName();
		final String className = "cls" + rootCls.getSimpleName();
		println("\nJavaObject %s(env, %s.className, %s);", jObjectName,
				className, jObjectRef);
		generateReadCodeForChildren(structName, rootCls, jObjectRef,
				jObjectName);
	}

	private void generateReadCodeForChildren(final String structName,
			final MyClass rootCls, final String parentRefOrObjectName,
			final String jObjectName) {
		for (MyField f : rootCls.getFields()) {
			final String xmlElementName = f.getNameXmlElement();
			if (!f.isPrimitive()) {
				// 2 options: List<Class> or Class
				if (f.getFieldClass().isAssignableFrom(List.class)) {
					final String jChildobjectName = "j"
							+ rootCls.getSimpleName() + xmlElementName + "List";
					// List Data type can be a primitive or Class
					final Class<?> listDataType = f.getTypeXmlElement();
					if (isPrimitiveDataType(listDataType)) {
						//
						final String primitiveListType = getPrimitiveDataTypeString(listDataType);
						final String javaListType = "Java"
								+ firstLetterUppercase(primitiveListType)
								+ "List";
						final String getLisMethodName = "get"
								+ f.getNameXmlElement();
						println("%s %s(env, %s,\"%s\");", javaListType,
								jChildobjectName, jObjectName, getLisMethodName);
						final String jChildobjectNameSize = jChildobjectName
								+ "Size";
						//
						println("jint %s = %s.getSize());",
								jChildobjectNameSize, jChildobjectName);
						println("for(int i=0; i<%s; i++) {",
								jChildobjectNameSize);
						println("\tj%s value = %s.get(i);", primitiveListType,
								jChildobjectName);
						println("}");
					} else {

						// Strange enough, for object, the field type is not set. 
						// So, the generic type of the list is used.
						ParameterizedType listType = (ParameterizedType) f.getField().getGenericType();
						Class<?> listGenericClass = (Class<?>) listType
								.getActualTypeArguments()[0];

						System.out.println("XmlElementName: " + xmlElementName
								+ ", ListDataType: " + listDataType.getName()
								+ ", listGenericClass: "
								+ listGenericClass.getName());

						if (listDataType.isAssignableFrom(String.class)) {
							throw new AssertionError("LIST-string");
						} else {
							throw new AssertionError("LIST-object");
						}
					}
				} else {
					// Create instance of Java Object
					// Example:
					// JavaObject jDoorSettings(env, clsDoorSettings.className,
					// clsDoorSettings.getJObjectFrom(jAppSettings));
					final String jChildobjectName = "j" + f.getNameXmlElement();
					final String childClassName = "cls" + f.getNameXmlElement();
					println("\nJavaObject %s(env, %s.className, %s.getJObjectFrom(%s));",
							jChildobjectName, childClassName, childClassName,
							jObjectName);
					//
					MyClass childClass = mgr.get(f.getFieldClass());
					generateReadCodeForChildren(
							structName + "." + f.getNameXmlElement(),
							childClass, jChildobjectName, jChildobjectName);
				}
			} else {
				// Example:
				// settings.RoofSettings.Length =
				// jRoofSettings.getInt("getLength");
				final String parentObjectName = parentRefOrObjectName
						.substring(1);
				final String primitiveMethodName = "get"
						+ f.getFieldObjectTypeFirstLetterUppercase();
				final String parameterName = f.getNameXmlElement();
				final String getMethodName = f.getGetMethod();
				// Create call that call get method on JavaObject
				println(structName + ".%s = %s.%s(\"%s\");", parameterName,
						parentRefOrObjectName, primitiveMethodName,
						getMethodName);

			}
		}
	}

	private String firstLetterUppercase(final String primitiveListType) {
		return primitiveListType.substring(0, 1).toUpperCase()
				+ primitiveListType.substring(1);
	}

	private boolean isPrimitiveDataType(final Class<?> cls) {
		return getPrimitiveDataTypeString(cls) != null;
	}

	private String getPrimitiveDataTypeString(final Class<?> cls) {
		if (cls == null) {
			return null;
		}
		for (BasicDatatype datatype : primitiveDataTypes) {
			if (cls.isAssignableFrom(datatype.cls)) {
				return datatype.basictype;
			}
		}
		return null;
	}

	// private void generateReadCodeForChildren(final MyClass rootCls,
	// final String parentRefOrObjectName, final String jObjectName) {
	// for (MyMethod m : rootCls.getMethods()) {
	// if (m.getGetMethod().getReturnType().isPrimitive() == false) {
	// // Create instance of Java Object
	// // Example:
	// // JavaObject jDoorSettings(env, clsDoorSettings.className,
	// // clsDoorSettings.getJObjectFrom(jAppSettings));
	// final String jChildobjectName = "j" + m.getParameterName();
	// final String childClassName = "cls" + m.getParameterName();
	// println("\nJavaObject %s(env, %s.className, %s.getJObjectFrom(%s));",
	// jChildobjectName, childClassName, childClassName,
	// jObjectName);
	// //
	// MyClass childClass = mgr.get(m.getGetMethod().getReturnType());
	// final String jChildObjectName = "j"
	// + childClass.getSimpleName();
	// generateReadCodeForChildren(childClass, jChildobjectName,
	// jChildObjectName);
	// } else {
	// // Example:
	// // settings.RoofSettings.Length =
	// // jRoofSettings.getInt("getLength");
	// final String parentObjectName = parentRefOrObjectName
	// .substring(1);
	// final String primitiveMethodName = "get"
	// + m.getReturnTypeGetMethodFirstLetterUppercase();
	// final String parameterName = m.getParameterName();
	// final String getMethodName = m.getGetMethod().getName();
	// // Create call that call get method on JavaObject
	// println("settings.%s.%s = %s.%s(\"%s\");", parentObjectName,
	// parameterName, parentRefOrObjectName,
	// primitiveMethodName, getMethodName);
	//
	// }
	// }
	// }

}
