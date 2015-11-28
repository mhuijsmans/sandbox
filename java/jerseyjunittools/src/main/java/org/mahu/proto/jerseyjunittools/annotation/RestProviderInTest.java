package org.mahu.proto.jerseyjunittools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestProviderInTest {

	// Initially I wanted to use Object instead of Class<?>, but that is not allowed.
	// see: http://stackoverflow.com/questions/1458535/which-types-can-be-used-for-java-annotation-members
	public Class<?> provider() default RestProviderInTest.class;

	public Class<?> [] providers() default {};

}
