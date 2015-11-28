package org.mahu.proto.socketthroughputtest;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

public class TestByteBuffer {
	
	@Test
	public void exploreByteBuffer() {
		int size = 1024;
		ByteBuffer b = ByteBuffer.allocateDirect(1024);
		assertInitialValues(size, b);
		//
		int bytesWritten = 3;
		b.put(new byte[bytesWritten], 0, bytesWritten);
		assertValuesWithNrOfBytesWritten(size, b, bytesWritten);	
		//
		b.flip();
		assertEquals(size, b.capacity());
		assertEquals(bytesWritten, b.limit());	
		assertEquals(0, b.position());
		assertEquals(bytesWritten, b.remaining());
		//
		b.clear();
		assertInitialValues(size, b);	
		//
		bytesWritten = 5;
		b.position(bytesWritten);
		assertValuesWithNrOfBytesWritten(size, b, bytesWritten);			
	}

	protected void assertValuesWithNrOfBytesWritten(int size, ByteBuffer b,
			int bytesWritten) {
		assertEquals(size, b.capacity());
		assertEquals(size, b.limit());		
		assertEquals(bytesWritten, b.position());
		assertEquals(size-bytesWritten, b.remaining());
	}

	protected void assertInitialValues(int size, ByteBuffer b) {
		assertEquals(true, b.isDirect());
		assertEquals(size, b.capacity());
		assertEquals(size, b.limit());		
		assertEquals(0, b.position());
		assertEquals(size, b.remaining());
	}

}
