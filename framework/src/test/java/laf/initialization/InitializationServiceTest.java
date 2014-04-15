package laf.initialization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Iterables;

public class InitializationServiceTest {

	private final class Fail implements Answer<Object> {
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			fail();
			return null;
		}
	}

	InitializationService service;

	@Before
	public void setup() {
		service = new InitializationService();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testRunInitializers() throws Exception {
		Initializer init1 = mock(Initializer.class, "init1");
		Initializer init2 = mock(Initializer.class, "init2");

		when(init1.getRepresentingClass()).thenReturn((Class) Integer.class);
		when(init1.getRepresentingClass()).thenReturn((Class) Float.class);
		when(init1.isBefore(init2)).thenReturn(true);
		service.runInitializers(init1, Arrays.asList(init1, init2));

		InOrder order = inOrder(init1, init2);
		order.verify(init1).run();
		order.verify(init2).run();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = RuntimeException.class)
	public void testRunInitializersLoop() throws Exception {
		Initializer init1 = mock(Initializer.class, "init1");
		Initializer init2 = mock(Initializer.class, "init2");

		when(init1.getRepresentingClass()).thenReturn((Class) Integer.class);
		when(init1.getRepresentingClass()).thenReturn((Class) Float.class);
		when(init1.isBefore(init2)).thenReturn(true);
		when(init2.isBefore(init1)).thenReturn(true);
		doAnswer(new Fail()).when(init1).run();
		doAnswer(new Fail()).when(init2).run();

		service.runInitializers(init1, Arrays.asList(init1, init2));
	}

	@Test
	public void testCreateInitializersProvider() throws Exception {
		Initializer init1 = mock(Initializer.class);
		InitializerProvider provider = mock(InitializerProvider.class);
		when(provider.getInitializers()).thenReturn(Arrays.asList(init1));
		Iterable<Initializer> initializers = service
				.createInitializers(provider);
		assertEquals(1, Iterables.size(initializers));
	}

	private static class TestInitializer1 {
		public boolean initialized;

		@LafInitializer
		public void init1() {
			initialized = true;
		}

	}

	private static class TestInitializer2 {
		public boolean initialized;

		@LafInitializer(after = TestInitializer1.class)
		public void init2() {
			initialized = true;
		}
	}

	@Test
	public void testCreateInitializersMethod() throws Exception {
		TestInitializer1 provider1 = new TestInitializer1();
		TestInitializer2 provider2 = new TestInitializer2();
		Iterable<Initializer> initializers = service.createInitializers(Arrays
				.asList(provider1, provider2));

		assertEquals(2, Iterables.size(initializers));
		Initializer init1 = null;
		Initializer init2 = null;

		for (Initializer initializer : initializers) {
			if (initializer.getRepresentingClass().equals(
					TestInitializer1.class)) {
				assertNull(init1);
				init1 = initializer;
			} else if (initializer.getRepresentingClass().equals(
					TestInitializer2.class)) {
				assertNull(init2);
				init2 = initializer;
			} else {
				fail("unexpected initializer");
			}
		}

		assertFalse(init2.isBefore(init1));
		assertFalse(init1.isAfter(init2));
		assertTrue(init1.isBefore(init2));
		assertTrue(init2.isAfter(init1));

		init1.run();
		init2.run();

		assertTrue(provider1.initialized);
		assertTrue(provider2.initialized);

	}

	@Test
	public void testCheckUnique() throws Exception {
		Initializer init1 = mock(Initializer.class);
		Initializer init2 = mock(Initializer.class);
		assertTrue(service.checkUnique(Arrays.asList(init1)));
		assertTrue(service.checkUnique(Arrays.asList(init1, init2)));
		assertFalse(service.checkUnique(Arrays.asList(init1, init1)));
	}

	@Test
	public void testCalculateBeforeRelation() throws Exception {
		Initializer init1 = mock(Initializer.class);
		Initializer init2 = mock(Initializer.class);
		when(init1.isBefore(init2)).thenReturn(true);

		Map<Initializer, Set<Initializer>> map = service
				.calculateBeforeRelation(Arrays.asList(init1, init2));
		assertEquals(2, map.size());
		assertTrue(map.get(init2).isEmpty());
		assertEquals(1, map.get(init1).size());
		assertTrue(map.get(init1).contains(init2));
	}
}
