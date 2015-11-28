package org.mahu.proto.mockitotest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test trying out Mockito test cases.
 */
 // Ref (examples): http://docs.mockito.googlecode.com/hg/latest/org/mockito/Mockito.html
/**
 * RunWith allows use of annotations
 */
@RunWith(MockitoJUnitRunner.class)
public class AppTest {

	/**
	 * Non-mockito test case
	 */
	@Test
	public void testDeadAuthor() {
		Author deadAuthor = new AuthorImpl(false);
		Book book = new BookImpl(deadAuthor);
		assertTrue(book.isAuthorAlive() == false);
	}

	/**
	 * Non-mockito test case
	 */
	@Test
	public void testAliveAuthor() {
		{
			Author author = new AuthorImpl(true);
			Book book = new BookImpl(author);
			assertTrue(book.isAuthorAlive() == true);
		}
		{
			Author author = new AuthorImpl();
			Book book = new BookImpl(author);
			assertTrue(book.isAuthorAlive() == true);
		}
	}

	/**
	 * Non-mockito test case
	 */
	@Test
	public void testAuthorIs100() {
		Author deadAuthor = new AuthorImpl(false);
		Book book = new BookImpl(deadAuthor);
		assertTrue(book.authorAge() == AuthorImpl.AUTHOR_AGE);
	}

	/**
	 * Non-mockito test case
	 */
	@Test
	public void testMockDeadAuthor() {
		Author author = mock(AuthorImpl.class);
		when(author.isAlive()).thenReturn(false);
		Book book = new BookImpl(author);
		assertTrue(book.isAuthorAlive() == false);
	}

	/**
	 * In this test I have multiple when-clauses for the same method.
	 */
	@Test
	public void testMockAgeAuthorUsingClass() {
		Author author = mock(AuthorImpl.class);
		Book book = new BookImpl(author);
		//
		when(author.age()).thenReturn(50);
		assertTrue(book.authorAge() == 50);
		//
		when(author.age()).thenReturn(75);
		assertTrue(book.authorAge() == 75);
	}

	/**
	 * In this test I create a Mock object using an interface.
	 */
	@Test
	public void testMockAgeAuthorUsingInterface() {
		Author author = mock(Author.class);
		Book book = new BookImpl(author);
		//
		when(author.age()).thenReturn(50);
		assertTrue(book.authorAge() == 50);
	}

	/**
	 * Test that method on Mock is called and not called.
	 */
	@Test
	public void testMockMethodIscalled() {
		Author author = mock(Author.class);
		Book book = new BookImpl(author);
		//
		when(author.age()).thenReturn(50);
		assertTrue(book.authorAge() == 50);
		//
		Mockito.verify(author).age();
		Mockito.verify(author, Mockito.times(0)).isAlive();
	}

	/**
	 * In this test the Mock obj throws an Exception.
	 */
	@Test(expected = RuntimeException.class)
	public void testMockThrowException() {
		Author author = mock(Author.class);
		Book book = new BookImpl(author);
		//
		when(author.age()).thenThrow(new RuntimeException());
		book.authorAge();
	}

	/**
	 * This test creates a spy (a wrapper) on an existing object.
	 */
	@Test
	public void testMockSpyOnAuthor() {
		Author deadAuthor = new AuthorImpl(false);
		Author spy = Mockito.spy(deadAuthor);
		Book book = new BookImpl(spy);
		when(spy.isAlive()).thenReturn(true);
		assertTrue(book.isAuthorAlive() == true);
	}

	/**
	 * This test creates a spy (a wrapper) on an existing object using
	 * annotation.
	 */
	@Spy
	private Author spyAuthor = new AuthorImpl();

	@Test
	public void testMockAnnotatedSpyOnAuthor() {
		Book book = new BookImpl(spyAuthor);
		when(spyAuthor.isAlive()).thenReturn(true);
		assertTrue(book.isAuthorAlive() == true);
	}

	/**
	 * Throw exception using Mockito
	 */
	@Test(expected = IOException.class)
	public void test_OutputStreamWriter_rethrows_an_exception_from_OutputStream()
			throws IOException {
		OutputStream mock = mock(OutputStream.class);
		OutputStreamWriter osw = new OutputStreamWriter(mock);
		Mockito.doThrow(new IOException()).when(mock).close();
		osw.close();
	}

}
