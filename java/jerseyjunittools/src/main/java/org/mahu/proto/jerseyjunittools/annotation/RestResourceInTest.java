package org.mahu.proto.jerseyjunittools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestResourceInTest {

	/**
	 * I want default value to be null, but that is not allowed by Java.
	 */
	public Class<?> resource() default RestResourceInTest.class;	
	public Class<?> []resources() default {};

}
