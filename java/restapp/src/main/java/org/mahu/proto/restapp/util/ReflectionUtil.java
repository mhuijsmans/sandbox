package org.mahu.proto.restapp.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.annotation.ProcessTaskResult;

public class ReflectionUtil {

	public static class ReflectionUtilException extends Exception {
		private static final long serialVersionUID = 1L;
		ReflectionUtilException(String msg) {
			super(msg);
		}
	}

	public static boolean DoesClassExist(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	public static final Class<? extends ProcessTask> GetProcessTask(final String classnameProcessTask) {
		try {
			Class<?> cls = Class.forName(classnameProcessTask);
			if (ProcessTask.class.isAssignableFrom(cls)) {
				return (Class<? extends ProcessTask>)cls;
			} else {
				throw new IllegalArgumentException("Class doesn't implement ProcessTask interface");
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Class not found: "+classnameProcessTask);
		}		
	}

	/**
	 * A class that implements ProcessTask is expected to have a enum called
	 * Result. This method retrieves the enum const for a class & resultValue.
	 * (which must correspond to a EnumValue).
	 * 
	 * Note that Enum.valueOf(..,name) is name case sensitive. This method convert enumValue to upperclass.
	 * So the enum Result { ..,.. } is expected to have uppercase value names.  
	 * 
	 * @param className
	 * @param enumValue
	 * @return
	 * @throws ReflectionUtilException
	 */
	public static Enum<?> GetEnumForProcessTaskImpl(final String className,
			String enumValue) throws ReflectionUtilException {
		enumValue = enumValue.toUpperCase();
		try {
			Class<?> c2 = Class.forName(className);
			if (ProcessTask.class.isAssignableFrom(c2)) {
				String classNameEnum = className + "$Result";
				Class<?> c3 = Class.forName(classNameEnum);
				if (c3.isEnum()) {
					Class<? extends Enum> enumClass = (Class<? extends Enum>) c3;
					try {
						@SuppressWarnings("unchecked")
						Enum<?> javaEnumValue = Enum.valueOf(enumClass, enumValue);
						return javaEnumValue;
					} catch (IllegalArgumentException e) {
						throw new ReflectionUtilException(
								"Class "
										+ className
										+ " contains the required enum class, but not enum value: "
										+ enumValue);
					}
				}
				throw new ReflectionUtilException(
						"Class doesn't contain the required enum class: "
								+ className);
			} else {
				throw new ReflectionUtilException(
						"Class doesn't implement interface: " + className);
			}
		} catch (ClassNotFoundException e) {
			throw new ReflectionUtilException("Class not found: " + className);
		}
	}

	private  static Enum<?> GetEnumForFieldWithAnnotationProcessTaskResult(
			final String className, String enumName)
			throws ReflectionUtilException {
		try {
			Class<?> c2 = Class.forName(className);
			if (ProcessTask.class.isAssignableFrom(c2)) {
				Field[] declaredFields = c2.getDeclaredFields();
				for (Field field : declaredFields) {
					System.out.println("Found declaredFields: "
							+ field.getName());
					if (field.isAnnotationPresent(ProcessTaskResult.class)) {
						Class<?> cls = field.getType();
						if (cls.isEnum()) {
							@SuppressWarnings({ "rawtypes", "unchecked" })
							Class<? extends Enum> enumClass = (Class<? extends Enum>) field
									.getType();
							@SuppressWarnings("unchecked")
							Enum<?> enumValue = Enum.valueOf(enumClass,
									enumName);
							return enumValue;
						} else {
							throw new ReflectionUtilException(
									"Class uses annotation ProcessTaskResult with invalid field: "
											+ field.getType().getName()
											+ ", only Enum is supported");
						}
					}
				}
				String classNameEnum = className + "$Result";
				Class<?> c3 = Class.forName(classNameEnum);
				PrintAnnotationsOnClass(c2);
				PrintInnerClasses(c2);
				PrintAnnotationsOnClass(c3);
				if (c3.isAnnotationPresent(ProcessTaskResult.class)) {
					Class<? extends Enum> enumClass = (Class<? extends Enum>) c3;
					@SuppressWarnings("unchecked")
					Enum<?> enumValue = Enum.valueOf(enumClass, enumName);
					return enumValue;
				}
				// for (Element element :
				// c2.getElementsAnnotatedWith(ProcessTaskResult.class)) {
				// MyAnnotation myAnnotation =
				// element.getAnnotation(MyAnnotation.class);
				// if (myAnnotation != null) {
				// doSomething(myAnnotation, element);
				// }
				// }
				throw new ReflectionUtilException(
						"Class doesn't contains a field with the annotation: "
								+ ProcessTaskResult.class.getName());
			} else {
				throw new ReflectionUtilException(
						"Class doesn't implement interface: "
								+ ProcessTask.class.getName());
			}
		} catch (ClassNotFoundException e) {
			throw new ReflectionUtilException("Class not found: " + className);
		}
	}

	public static void PrintAnnotationsOnClass(Class<?> cls) {
		System.out.println("Annotation for class: " + cls.getName());
		Annotation[] annotations = cls.getAnnotations();
		if (annotations.length == 0) {
			System.out.println("  None");
		} else {
			for (Annotation a : annotations) {
				System.out.println("  annotation: " + a.getClass().getName());
			}
		}
	}

	public static void PrintInnerClasses(Class<?> cls) {
		System.out.println("Innerclasses for class: " + cls.getName());
		Class<?>[] innerClasses = cls.getDeclaredClasses();
		if (innerClasses.length == 0) {
			System.out.println("- None");
		} else {
			for (Class<?> declaredClass : innerClasses) {
				System.out.println("- " + declaredClass.getName());
				System.out.println("- is enum: " + declaredClass.isEnum());
				//
				if (declaredClass.isEnum()) {
					Class<? extends Enum> enumClass = (Class<? extends Enum>) declaredClass;
				}
				//
				Annotation[] annotations = declaredClass.getAnnotations();
				for (Annotation a : annotations) {
					System.out.println("- - with annotation: "
							+ a.getClass().getName());
				}
			}
		}
	}
}
