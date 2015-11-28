package org.mahu.proto.drools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mahu.proto.drools.model.Barcode;
import org.mahu.proto.drools.model.BarcodeType;
import org.mahu.proto.drools.model.TheName;

public class BarcodeTests {

	private static final boolean PRINT_EXCEPTION_DETAILS = false;

	private KnowledgeBase kbase;
	private StatefulKnowledgeSession ksession;

	@Before
	public void createKSession() {
		kbase = DroolsUtil.createKnowledgeBase("Barcode.drl");
		ksession = kbase.newStatefulKnowledgeSession();
	}

	@After
	public void cleanup() {
		kbase = null;
		ksession = null;
	}

	@Test
	public void createKnowledgeBase_unknownRules_exception() {
		final String RULES = "unknown.drl";
		try {
			DroolsUtil.createKnowledgeBase(RULES);
		} catch (Exception e) {
			printCause(e);
			// When rules are not found, Drools throws RuntimeException wrapping
			// a FileNotFoundException
			// This may change between versions
			assertException(RuntimeException.class, "Unable to get LastModified for ClasspathResource", e);
			assertException(FileNotFoundException.class, "'" + RULES + "\' cannot be opened because it does not exist",
					e.getCause());
			assertEquals(null, e.getCause().getCause());
		}
	}

	void assertException(final Class<?> expectedClass, final String expectedMessage, final Throwable e) {
		assertNotNull(e);
		assertEquals(expectedClass, e.getClass());
		if (expectedMessage != null) {
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	@Test
	public void createKnowledgeBase_invalidRules_exception() {
		final String RULES = "invalid_rules.drl";
		try {
			DroolsUtil.createKnowledgeBase(RULES);
		} catch (Exception e) {
			printCause(e);
			// When rules are invalid, Drools provide an error reporting mechanism
			assertException(IllegalArgumentException.class, "[[file=invalid_rules.drl][Unable to resolve ObjectType 'SomeUnknownObject' : [Rule name='Invalid Rule']]]", e);
			assertEquals(null, e.getCause());
		}
	}

	private void printCause(Throwable e) {
		if (PRINT_EXCEPTION_DETAILS) {
			if (e != null) {
				System.out.println(e.getClass() + ": " + e.getMessage());
				e.printStackTrace();
				printCause(e.getCause());
			}
		}
	}

	@Test
	public void rule100_valueMatches_ruleFires() {
		Barcode barcode = new Barcode("100", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 1 rule fires", 1, ksession.fireAllRules());
	}

	@Test
	public void rule101_prefixMatches_ruleFires() {
		Barcode barcode = new Barcode("101Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 1 rule fires", 1, ksession.fireAllRules());
	}

	@Test
	public void rule102_prefixAndTypeMatch_ruleFires() {
		Barcode barcode = new Barcode("102Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 1 rule fires", 1, ksession.fireAllRules());
	}

	@Test
	public void rule103_matchAddsNewFact_newFactFires() {
		Barcode barcode = new Barcode("103Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 2 rules fire", 2, ksession.fireAllRules());
	}

	@Test
	public void rule104_matchAddsNewFact_nameSetByRule() {
		Barcode barcode = new Barcode("104Rules", BarcodeType._1D);
		ksession.insert(barcode);
		TheName names = new TheName();
		ksession.insert(names);

		final int firedRulesCount = ksession.fireAllRules();
		final List<String> addedNames = names.getNames();
		//
		assertEquals("Expected that 1 rule fires", 1, firedRulesCount);
		assertEquals("Expected that 1 rule fires", 1, addedNames.size());
		assertEquals("104name", addedNames.get(0));
	}

	@Test
	public void rule105_helperMethodPrefixMatch_ruleFires() {
		Barcode barcode = new Barcode("105Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 1 rule fires", 1, ksession.fireAllRules());
	}

	@Test
	public void rule106_valueUpdatedInCode_notDetectedByDrools() {
		Barcode barcode = new Barcode("106Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 1 rule fires", 1, ksession.fireAllRules());
		assertEquals("Expected that 0 rule fires", 0, ksession.fireAllRules());
	}

	// Cascading test: a fact is added, that triggers a rule which updates a
	// fact (the Drools way) which next fires another rule.
	@Test
	public void rule107_valueUpdatedByRule_detectedByDroolsDoubleFire() {
		Barcode barcode = new Barcode("107Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 2 rule fires (2nd because of updated value): o", 2, ksession.fireAllRules());
	}

	// Cascading test: a fact is added, that triggers a rule (1) which updates a
	// fact (the Drools way) which next fires another rule (2) which updates a
	// fact which trigger yet another rule (3).
	@Test
	public void rule108_valueUpdatedByRuleTriggeringAnotherRuleAlsoWithUpdate_detectedByDroolsTrippleFire() {
		Barcode barcode = new Barcode("108Rules", BarcodeType._1D);
		ksession.insert(barcode);
		//
		assertEquals("Expected that 3 rule fires (because of cascading updates): o", 3, ksession.fireAllRules());
	}

}