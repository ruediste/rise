package laf.core.web.resource;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceTypeTest {

	@Test
	public void extractExtension() {
		assertEquals("css", ResourceType.fromExtension("style.css").getIdentifier());
	}

	@Test
	public void extractExtensionSingleLetter() {
		assertEquals("c", ResourceType.fromExtension("style.c").getIdentifier());
	}

	@Test(expected = RuntimeException.class)
	public void extractExtensionEndingInDot() {
		assertEquals("css", ResourceType.fromExtension("style.").getIdentifier());
	}

	@Test
	public void equality() {
		ResourceType t1 = ResourceType.fromExtension("foo.css");
		ResourceType t2 = ResourceType.fromExtension("bar.css");
		ResourceType t3 = ResourceType.fromExtension("foobar.js");

		assertEquals(t1, t2);
		assertEquals(t1.hashCode(), t2.hashCode());
		assertNotEquals(t1, t3);
		assertNotEquals(t1.hashCode(), t3.hashCode());
	}
}
