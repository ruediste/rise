package laf.core.web.resource;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceBundleTest {

	@Test
	public void testEquality() {
		ResourceBundle a1 = new ResourceBundle(ResourceType.valueOf("a"), "a");
		ResourceBundle a2 = new ResourceBundle(ResourceType.valueOf("a"), "a");
		ResourceBundle b = new ResourceBundle(ResourceType.valueOf("b"), "b");

		assertEquals(a1, a2);
		assertEquals(a1.hashCode(), a2.hashCode());
		assertNotEquals(a1, b);
		assertNotEquals(a1.hashCode(), b.hashCode());
	}
}
