package laf.core.web.resource;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceTypeTest {

	@Test
	public void extractExtension() {
		assertEquals("css", ResourceType.of("style.css").getIdentifier());
	}

	@Test
	public void extractExtensionSingleLetter() {
		assertEquals("c", ResourceType.of("style.c").getIdentifier());
	}

	@Test(expected = RuntimeException.class)
	public void extractExtensionEndingInDot() {
		assertEquals("css", ResourceType.of("style.").getIdentifier());
	}

	@Test
	public void equality() {
		ResourceType t1 = ResourceType.of("foo.css");
		ResourceType t2 = ResourceType.of("bar.css");
		ResourceType t3 = ResourceType.of("foobar.js");

		assertEquals(t1, t2);
		assertEquals(t1.hashCode(), t2.hashCode());
		assertNotEquals(t1, t3);
		assertNotEquals(t1.hashCode(), t3.hashCode());
	}
}
