package laf.module.pattern;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassPatternTest {

	@Test
	public void test() {
		ClassPattern pattern = new ClassPattern("foo.bar.Test");
		assertEquals(300, pattern.getScore());
		assertTrue(pattern.matches("foo.bar.Test"));
		assertFalse(pattern.matches("foo.bar.Tes"));
	}
}
