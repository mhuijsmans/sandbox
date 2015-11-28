package org.mahu.proto.junit;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mahu.proto.junit.annotation.MyMethodAnnotation;

public class MyTestRule implements TestRule {

	/**
	 * This method is called for every method annotated with @Test
	 */
	public Statement apply(Statement arg0, Description arg1) {
		System.out.println("--------------------------------");
		System.out.println("Statement: " + arg0);
		System.out.println("Statement.class: " + arg0.getClass().getName());
		descriptionInfo(arg1);
		return new MyStatement(arg0);
	}

	private void descriptionInfo(Description desc) {
		System.out.println("Description: " + desc);
		System.out.println("Description.class: " + desc.getClass().getName());
		//
		Collection<Annotation> col = desc.getAnnotations();
		for (Annotation ann : col) {
			Class<? extends Annotation> annCls = ann.annotationType();
			System.out.println("Description.annotationClass: " + annCls);
		}
		//
		MyMethodAnnotation annotation = desc
				.getAnnotation(MyMethodAnnotation.class);
		if (annotation != null) {
			String param = annotation.param();
			System.out
					.println("Description.MyMethodAnnotation.param: " + param);
		}
	}

	/**
	 * MyStatement evaluate() is invoked for every test case.
	 */
	static class MyStatement extends Statement {
		private Statement statement;

		MyStatement(final Statement aStatement) {
			statement = aStatement;
		}

		@Override
		public void evaluate() throws Throwable {
			System.out.println("MyStatement.evaluate()");
			statement.evaluate();
		}

	}

}
