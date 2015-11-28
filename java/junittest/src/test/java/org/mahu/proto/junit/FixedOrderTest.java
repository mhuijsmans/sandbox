package org.mahu.proto.junit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

/**
 * FixMethodOrder executes test in a defined order.
 * Use with case. Test shall be written indepedant of each other.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FixedOrderTest {
	
	@Test
	public void secondTest() {
		assertTrue(true);
	}	
	
	@Test
	public void firstTest() {
		assertTrue(true);
	}

}
