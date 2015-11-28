package org.nahe.proto.test1;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testCalculator() {
		Calculator.sqrt(0, 0);
	}
	
	public void testVoertuig() {
		IVoertuig voertuig1 = new Fiets();
		System.out.println("#doors="+voertuig1.getNrOFDoors());
		
		IVoertuig voertuig2 = new Auto();
		System.out.println("#doors="+voertuig2.getNrOFDoors());		
	}	
	
	public void testVoertuigen() {
		IVoertuig[] voertuigen = new IVoertuig[] { new Fiets(), new Auto() };
		for(IVoertuig v : voertuigen) {
			System.out.println("#doors="+v.getNrOFDoors());
		}		
	}	
}
