package org.mahu.proto.spring;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mahu.proto.spring.test.TestComponent1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context-test.xml" })
public class SpringTest {

	@Inject
	@Named("instance1")
	private MessageService1 service;
	
	@Autowired
	ApplicationContext context;

	@Test
	public void injectMeTest() {
		assertTrue(service!=null);
		assertTrue(service.getName().equals(TestComponent1.NAME));
	}
	
	@Test
	public void contextResolvedTest() {
		assertTrue(context !=null);
	}	
	
	@Test
	public void injectTest() {
		/**
		 * I needed to define this class in the application....xml file to get
		 * Spring to find it. 
		 * Somehow feels good, so there are no accidental components
		 */
		MessagePrinter2 p =  context.getBean(MessagePrinter2.class);
		assertTrue(p.getService1() != null);
		assertTrue(p.getService2() != null);
		assertTrue(p.getService3() != null);
		// Not injected component, but regular class
		assertTrue(p.getDummy() != null);
	}	
}
