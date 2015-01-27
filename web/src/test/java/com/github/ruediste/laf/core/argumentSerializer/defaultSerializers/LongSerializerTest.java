package com.github.ruediste.laf.core.argumentSerializer.defaultSerializers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.LongSerializer;

@RunWith(MockitoJUnitRunner.class)
public class LongSerializerTest {

	@Mock
	AnnotatedType LongType;
	@Mock
	AnnotatedType longType;

	LongSerializer serializer = new LongSerializer();

	@Before
	public void before() {
		when(LongType.getType()).thenReturn(Long.class);
		when(longType.getType()).thenReturn(Long.TYPE);
	}

	@Test
	public void testGenerate() throws Exception {
		assertEquals("1", serializer.generate(LongType, 1L));
		assertEquals("1", serializer.generate(longType, 1L));
		assertEquals("null", serializer.generate(LongType, null));
	}

	@Test
	public void testParse() throws Exception {
		assertEquals(1L, serializer.parse(LongType, "1").get());
		assertEquals(1L, serializer.parse(longType, "1").get());
		assertEquals(null, serializer.parse(LongType, "null").get());
	}
}
