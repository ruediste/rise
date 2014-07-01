package laf.base.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.List;

import laf.base.configuration.ConfigurationValueParsingService;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

public class ConfigurationValueParsingServiceTest {
	ConfigurationValueParsingService service;

	@Before
	public void setup() {
		service = new ConfigurationValueParsingService();
	}

	@Test
	public void testSimple() {
		assertEquals("Hello World",
				service.parse(TypeToken.of(String.class), "Hello World"));
	}

	@Test
	public void testInteger() {
		assertEquals(Integer.valueOf(26),
				service.parse(TypeToken.of(Integer.class), "26"));
	}

	@Test
	public void testListSingle() {
		TypeToken<List<String>> listType = new TypeToken<List<String>>() {
		};
		Object result = service.parse(listType, "Hello\\, World");
		assertTrue(List.class.isAssignableFrom(result.getClass()));
		List<?> list = (List<?>) result;
		assertEquals(1, list.size());
		assertEquals("Hello, World", list.get(0));
	}

	@Test
	public void testListMulti() {
		TypeToken<List<String>> listType = new TypeToken<List<String>>() {
			private static final long serialVersionUID = 1L;
		};
		Object result = service.parse(listType, "Hello, World漢字");
		assertTrue(List.class.isAssignableFrom(result.getClass()));
		List<?> list = (List<?>) result;
		assertEquals(2, list.size());
		assertEquals("Hello", list.get(0));
		assertEquals("World漢字", list.get(1));
	}
	
	@Test
	public void testDeQueueMulti() {
		TypeToken<Deque<String>> queueType = new TypeToken<Deque<String>>() {
			private static final long serialVersionUID = 1L;
		};
		Object result = service.parse(queueType, "Hello, World漢字");
		assertTrue(Deque.class.isAssignableFrom(result.getClass()));
		Deque<?> queue = (Deque<?>) result;
		assertEquals(2, queue.size());
		assertEquals("Hello", Iterables.get(queue, 0));
		assertEquals("World漢字", Iterables.get(queue, 1));
	}
}
