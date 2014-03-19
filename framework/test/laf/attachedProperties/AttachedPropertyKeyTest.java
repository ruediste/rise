package laf.attachedProperties;

import static org.junit.Assert.*;

import org.junit.Test;

public class AttachedPropertyKeyTest {

	@Test
	public void testCrud() {
		AttachedPropertyBearerBase bearer = new AttachedPropertyBearerBase();
		AttachedPropertyKey<Integer> key = new AttachedPropertyKey<>();
		assertFalse(key.isSet(bearer));
		key.set(bearer, 10);
		assertTrue(key.isSet(bearer));
		assertEquals(Integer.valueOf(10), key.get(bearer));
		key.set(bearer, 11);
		assertEquals(Integer.valueOf(11), key.get(bearer));
		key.set(bearer, null);
		assertTrue(key.isSet(bearer));
		key.clear(bearer);
		assertFalse(key.isSet(bearer));
	}
}
