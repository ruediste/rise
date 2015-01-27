package com.github.ruediste.laf.core.base;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.laf.core.base.PrefixMap;

public class PrefixMapTest {

	PrefixMap<Integer> map;

	@Before
	public void before() {
		map = new PrefixMap<>();
	}

	@Test
	public void testGetByKey() {
		map.put("a", 2);
		assertEquals(Integer.valueOf(2), map.get("a"));
	}

	@Test
	public void testGetByKeyAndSuffix() {
		map.put("a", 2);
		assertEquals(Integer.valueOf(2), map.get("ab"));
	}

	@Test
	public void testNoMatch() {
		map.put("b", 2);
		assertEquals(null, map.get("a"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewKeyPrefixOfOther() {
		map.put("abc", 3);
		map.put("ab", 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewKeysSuffixOfOther() {
		map.put("ab", 2);
		map.put("abc", 3);
	}

	@Test
	public void testGetByKeyAndSuffixTwo() {
		map.put("ab", 2);
		map.put("ad", 3);
		assertEquals(null, map.get("aac"));
		assertEquals(Integer.valueOf(2), map.get("abc"));
		assertEquals(null, map.get("acc"));
		assertEquals(Integer.valueOf(3), map.get("adc"));
		assertEquals(null, map.get("aec"));
	}
}
