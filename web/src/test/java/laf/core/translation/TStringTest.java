package laf.core.translation;

import static org.junit.Assert.*;

import org.junit.Test;

public class TStringTest {

	@Test
	public void createNoFallback() {
		TString s = new TString("foo");
		assertEquals("foo", s.getResourceKey());
		assertNull(s.getFallback());
	}

	@Test
	public void createWithFallback() {
		TString s = new TString("foo", "bar");
		assertEquals("foo", s.getResourceKey());
		assertEquals("bar", s.getFallback());
	}

	@Test
	public void testEquals() {
		TString s = new TString("foo", "bar");
		TString s1 = new TString("foo", "bar");
		TString s2 = new TString("foo1", "bar");
		TString s3 = new TString("foo", "bar1");
		assertEquals(s, s);
		assertEquals(s, s1);
		assertNotEquals(s, s2);
		assertNotEquals(s, s3);
	}
}
