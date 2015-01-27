package com.github.ruediste.laf.core.base;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.laf.core.base.DefaultClassNameMapping;
import com.google.common.base.Joiner;

public class DefaultClassNameMappingTest {

	DefaultClassNameMapping mapping;

	@Before
	public void setup() {
		mapping = new DefaultClassNameMapping();
	}

	@Test
	public void testNull() {
		mapping.initialize(null, null);
		assertEquals(getClass().getPackage().getName().replace('.', '/') + "/d"
				+ getClass().getSimpleName().substring(1),
				mapping.apply(getClass()));
	}

	@Test
	public void testSuffix() {
		mapping.initialize(null, "Test");
		String name = "/d" + getClass().getSimpleName().substring(1);
		name = name.substring(0, name.length() - "Test".length());
		assertEquals(
				getClass().getPackage().getName().replace('.', '/') + name,
				mapping.apply(getClass()));
	}

	@Test
	public void testBasePackage() {

		String[] parts = getClass().getPackage().getName().split("\\.");

		mapping.initialize(parts[0], null);
		String pck = Joiner.on("/").join(
				Arrays.asList(parts).stream().skip(1).toArray());
		assertEquals(pck + "/d" + getClass().getSimpleName().substring(1),
				mapping.apply(getClass()));
	}
}
