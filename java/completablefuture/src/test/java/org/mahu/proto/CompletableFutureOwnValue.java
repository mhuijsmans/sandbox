package org.mahu.proto;

import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
/**
 * Unit test where value completable future is set by test case 
 */
public class CompletableFutureOwnValue  {

	@Test
	public void new_isDoneReturnFalse()
    {
		CompletableFuture<String> cf = new CompletableFuture<>();
        assertFalse(cf.isDone());
    }
	
	@Test
	public void complete_isDoneReturnTrue() throws InterruptedException, ExecutionException
    {
		final String s = "string";
		CompletableFuture<String> cf = new CompletableFuture<>();
		cf.complete(s);
		
        assertTrue(cf.isDone());
        assertFalse(cf.isCompletedExceptionally());
        assertEquals(s, cf.get());
    }
	
	@Test
	public void obtrudeException_isDoneReturnTrue() throws InterruptedException, ExecutionException
    {
		final RuntimeException e1 = new RuntimeException();
		CompletableFuture<String> cf = new CompletableFuture<>();
		cf.obtrudeException(e1);
		
        assertTrue(cf.isDone());
        assertTrue(cf.isCompletedExceptionally());
        try {
        	cf.get();
        	fail("Exception expected");
        } catch (ExecutionException e2) {
        	// The exception if wrapper
        	assertEquals(e1, e2.getCause());
        }
    }	
}
