package laf.component.core.translation;

import static org.junit.Assert.*;

import org.junit.Test;

public class PStringTest {

	TString str = new TString("foo", null);

	@Test
	public void createNoParameters() {
		PString s = new PString(str);
		assertEquals("foo", s.getPattern().getResourceKey());
		assertTrue(s.getParameters().isEmpty());
	}

	@Test
	public void createSingleParameter() {
		PString s = new PString(str, "bar", 1);
		assertEquals("foo", s.getPattern().getResourceKey());
		assertEquals(1, s.getParameters().size());
		assertEquals(1, s.getParameters().get("bar"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createParameterInvalidCount() {
		new PString(str, "bar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createParameterInvalidType() {
		new PString(str, 1, 1);
	}
}
