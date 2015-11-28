package org.mahu.proto.regexp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class RegexpTest {

	@Test
	public void test0() {
		String pattern = ".\\.(so|dll)";
		Pattern p = Pattern.compile(pattern);
		//
		assertTrue(p.matcher("a.so").matches());
		assertTrue(p.matcher("a.dll").matches());
		assertTrue(p.matcher("..dll").matches());
		//
		assertFalse(p.matcher("a.os").matches());
		assertFalse(p.matcher(".os").matches());
		assertFalse(p.matcher("a.SO").matches());
		assertFalse(p.matcher("a.so.").matches());
	}

	@Test
	public void test1() {
		// \d is short for [0-9]
		String pattern = ".+\\.(so(\\.\\d+)?|dll)";
		Pattern p = Pattern.compile(pattern);
		//
		assertTrue(p.matcher("liba.so").matches());
		assertTrue(p.matcher("a.so").matches());
		assertTrue(p.matcher("a.so.2").matches());
		assertTrue(p.matcher("a.dll").matches());
		assertTrue(p.matcher("..dll").matches());
		//
		assertFalse(p.matcher("a.so9").matches());
		assertFalse(p.matcher("a.soa9").matches());
		assertFalse(p.matcher("a.os").matches());
		assertFalse(p.matcher(".os").matches());
		assertFalse(p.matcher("a.SO").matches());
		assertFalse(p.matcher("a.so.").matches());
		assertFalse(p.matcher("a.dll.2").matches());
		assertFalse(p.matcher("a.so.").matches());
		assertFalse(p.matcher("a.so9").matches());
		assertFalse(p.matcher("a.so.9a").matches());
	}

	@Test
	public void test3() {
		// case insensitive: (?i)
		String pattern = "(?i).\\.(so|dll)";
		Pattern p = Pattern.compile(pattern);
		//
		assertTrue(p.matcher("a.so").matches());
		assertTrue(p.matcher("a.dll").matches());
		//
		assertFalse(p.matcher("a.os").matches());
		assertTrue(p.matcher("a.SO").matches());
	}
}
