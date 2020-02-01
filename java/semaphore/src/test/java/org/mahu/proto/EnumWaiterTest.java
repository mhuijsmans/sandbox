package org.mahu.proto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class EnumWaiterTest 
{
	
	@Test
    public void testEnum()
    {
        assertTrue(PHASE.STEP0.compareTo(PHASE.STEP1) < 0);
        assertEquals(0, PHASE.STEP0.ordinal());
    }
	
	/**
	 * This class allows a user to wait for a certain enum value to have reached or 'exceeded' a certain value.   
	 */
	static class EnumValueWaiter1<T extends Enum<?>> {
		private final Semaphore sem;
		private int currentOridinal; 
		
		EnumValueWaiter1(T initialValue) {
			currentOridinal = initialValue.ordinal();
			sem = new Semaphore(currentOridinal);
		}
		
		/**
		 * Wait until the enum value matches the provided value or has passed it. 
		 * Wait for the specified time. 
		 * Return true, if the 
		 */
		boolean waitFor(final T waitForValue, long timeout, TimeUnit unit) {
			final int phaseSeqNr = waitForValue.ordinal();
			try {
				final boolean phaseMatchedOrPassed = sem.tryAcquire(phaseSeqNr, timeout, unit);
				if (phaseMatchedOrPassed) {
					sem.release(phaseSeqNr);
				}		
				return phaseMatchedOrPassed;				
			} catch (InterruptedException e) {
				// Ignore exception
				return false;
			}
		}
		
		private void update(final T newPhase) {
			final int newOrdinal = newPhase.ordinal();
			final int newPermits = newOrdinal - currentOridinal;
			currentOridinal = newOrdinal;
			sem.release(newPermits);
		}
		
	}
	
	@Test
    public void testPhaseWaiter()
    {
		EnumValueWaiter1<PHASE> pw = new EnumValueWaiter1<>(PHASE.STEP0);
        assertTrue(pw.waitFor(PHASE.STEP0, 1, TimeUnit.MILLISECONDS));
        assertFalse(pw.waitFor(PHASE.STEP1, 1, TimeUnit.MILLISECONDS));
        assertFalse(pw.waitFor(PHASE.STEP2, 1, TimeUnit.MILLISECONDS));
        assertFalse(pw.waitFor(PHASE.STEP3, 1, TimeUnit.MILLISECONDS));        
        
        pw.update(PHASE.STEP1);
        assertTrue(pw.waitFor(PHASE.STEP0, 1, TimeUnit.MILLISECONDS));
        assertTrue(pw.waitFor(PHASE.STEP1, 1, TimeUnit.MILLISECONDS));        
        assertFalse(pw.waitFor(PHASE.STEP2, 1, TimeUnit.MILLISECONDS));
        assertFalse(pw.waitFor(PHASE.STEP3, 1, TimeUnit.MILLISECONDS));
        
        pw.update(PHASE.STEP3);
        assertTrue(pw.waitFor(PHASE.STEP0, 1, TimeUnit.MILLISECONDS));
        assertTrue(pw.waitFor(PHASE.STEP1, 1, TimeUnit.MILLISECONDS));        
        assertTrue(pw.waitFor(PHASE.STEP2, 1, TimeUnit.MILLISECONDS));
        assertTrue(pw.waitFor(PHASE.STEP3, 1, TimeUnit.MILLISECONDS));        
    }	
	
	@Test
    public void testSemaphore()
    {
        Semaphore sem = new Semaphore(0);
        assertEquals(0, sem.availablePermits());
        sem.release();
        assertEquals(1, sem.availablePermits());
        
        assertTrue(sem.tryAcquire());
        assertEquals(0, sem.availablePermits());
    }
	
}
