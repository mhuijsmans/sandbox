package org.mahu.proto.spring;

import javax.inject.Named;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Annotating a class with @Configuration indicates that its primary purpose is
 * as a source of bean definitions. Furthermore, @Configuration classes allow
 * inter-bean dependencies to be defined by simply calling other @Bean methods
 * in the same class. ref:
 * http://docs.spring.io/spring/docs/3.2.x/spring-framework
 * -reference/html/beans.html#beans-standard-annotations
 */
@Configuration
@ComponentScan
public class App {

	/**
	 * @Bean results in registration of the return value as a bean within a
	 *       BeanFactory. MessagePrinter needs an instance of that interface Ref
	 *       Name will be name of the method.
	 *       Use of @bean is the sprint style
	 */
	@Bean
	MessageService2 mockMessageService() {
		return new MessageServiceImpl2_1();
	}

	static class MessageServiceImpl2_1 implements MessageService2 {
		private static int cnt = 0;
		private String name;

		MessageServiceImpl2_1() {
			name = "MImpl2_1-" + (cnt++);
		}

		public String getName() {
			return name;
		}
	}

	// JSR330: Named defines a component
	@Named("instance1")
	static class MessageServiceImpl1_1 implements MessageService1 {
		private static int cnt = 0;
		private String name;

		MessageServiceImpl1_1() {
			name = "MImpl1_1-" + (cnt++);
		}

		public String getName() {
			return name;
		}
	}

	@Named("instance2")
	static class MessageServiceImpl1_2 implements MessageService1 {
		private static int cnt = 0;
		private String name;

		MessageServiceImpl1_2() {
			name = "MImpl1_2-" + (cnt++);
		}

		public String getName() {
			return name;
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(
				App.class);
		// Wiring Spring style
		 MessagePrinter1 printer1 = context.getBean(MessagePrinter1.class);
		 printer1.printMessage();

		 // Wiring JSR 330 style
		MessagePrinter2 printer2 = context.getBean(MessagePrinter2.class);
		printer2.printMessage();
	}
}