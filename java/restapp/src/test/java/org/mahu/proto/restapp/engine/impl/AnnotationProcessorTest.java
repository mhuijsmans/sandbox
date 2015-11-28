package org.mahu.proto.restapp.engine.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.restapp.model.Node;
import org.mahu.proto.restapp.model.NodeRule;
import org.mahu.proto.restapp.model.ProcessDefinition;
import org.mahu.proto.restapp.model.ProcessTask;
import org.mahu.proto.restapp.model.impl.ProcessBuilderException;

public class AnnotationProcessorTest {

	final static Logger logger = LogManager.getLogger(AnnotationProcessorTest.class.getName());

	// TODO: Should be gmocked. This is ugly
	static class TestNode implements Node {

		@Override
		public String getName() {
			return "TestNode";
		}

		@Override
		public ProcessDefinition getProcessDefinition() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NodeRule when(Enum<?> aValue) throws ProcessBuilderException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void isFinal() throws ProcessBuilderException {
			// TODO Auto-generated method stub

		}

		@Override
		public Class<? extends ProcessTask> getProcessTaskClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNrOfResults() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getNrOfNodeRules() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public NodeRule getNodeRule(int idx) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NodeRule findRule(Enum<?> result) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<NodeRule> nodeRuleEnumeration() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	static class D {
	}

	static class C {
	}

	static class B {
		@Inject
		private C c;
	}

	static class A {
		@Inject
		private B b;

		@Inject
		private D d;
	}

	SessionImpl session;
	Map<String, Object> annotationData;

	@Before
	public void calledBeforeEachTest() {
		session = new SessionImpl(null, null);
		annotationData = new HashMap<>();
	}

	@Test
	public void AnnotateInstance_allAnotationResolved()
			throws InterruptedException {
		Node node = new TestNode();
		D d = new D();
		C c = new C();
		B b = new B();
		A a = new A();
		String keyClassB = B.class.getName();
		String keyClassC = C.class.getName();
		String keyClassD = D.class.getName();
		annotationData.put(keyClassB, b);
		annotationData.put(keyClassC, c);
		annotationData.put(keyClassD, d);

		logger.info("TestAnnotation(), B.key=" + keyClassB);
		logger.info("TestAnnotation(), C.key=" + keyClassC);
		logger.info("TestAnnotation(), D.key=" + keyClassD);

		AnnotationProcessor.AnnotateInstance(session, node, a, annotationData);

		assertTrue(a.b != null);
		assertTrue(a.b.c != null);
		assertTrue(a.d != null);
	}

	@Test
	public void AnnotateInstance_AnnotationNotFoundInFirstObject()
			throws InterruptedException {
		Node node = new TestNode();
		A a = new A();

		AnnotationProcessor.AnnotateInstance(session, node, a, annotationData);

		assertFalse(session.isStateJobsExistsOrPauzed());
	}

	@Test
	public void AnnotateInstance_AnnotationNotFoundInSecondObject()
			throws InterruptedException {
		Node node = new TestNode();
		D d = new D();
		B b = new B();
		A a = new A();
		String keyClassB = B.class.getName();
		String keyClassD = D.class.getName();
		annotationData.put(keyClassB, b);
		annotationData.put(keyClassD, d);

		logger.info("TestAnnotation(), B.key=" + keyClassB);
		logger.info("TestAnnotation(), D.key=" + keyClassD);

		AnnotationProcessor.AnnotateInstance(session, node, a, annotationData);

		assertFalse(session.isStateJobsExistsOrPauzed());
	}

}
