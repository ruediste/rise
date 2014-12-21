package laf.component.core.translation;

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
}
