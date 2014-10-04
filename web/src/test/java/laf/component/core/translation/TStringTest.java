package laf.component.core.translation;

import static org.junit.Assert.*;

import org.junit.Test;

public class TStringTest {

	@Test
	public void createNoParameters() {
		TString s = new TString("foo");
		assertEquals("foo", s.getResourceKey());
		assertTrue(s.getParameters().isEmpty());
	}

	@Test
	public void createSingleParameter() {
		TString s = new TString("foo", "bar", 1);
		assertEquals("foo", s.getResourceKey());
		assertEquals(1, s.getParameters().size());
		assertEquals(1, s.getParameters().get("bar"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createParameterInvalidCount() {
		new TString("foo", "bar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createParameterInvalidType() {
		new TString("foo", 1, 1);
	}
}
