package laf.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Iterables;

public class InitializationEngineTest {

	private final class Fail implements Answer<Object> {
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			fail();
			return null;
		}
	}

	InitializationEngine engine;

	@Before
	public void setup() {
		engine = new InitializationEngine();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testRunInitializers() throws Exception {
		Initializer init1 = mock(Initializer.class, "init1");
		Initializer init2 = mock(Initializer.class, "init2");

		when(init1.getComponentClass()).thenReturn((Class) Integer.class);
		when(init1.getComponentClass()).thenReturn((Class) Float.class);
		when(init1.isBefore(init2)).thenReturn(true);
		engine.runInitializers(Arrays.asList(init1, init2));

		InOrder order = inOrder(init1, init2);
		order.verify(init1).run();
		order.verify(init2).run();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = RuntimeException.class)
	public void testRunInitializersLoop() throws Exception {
		Initializer init1 = mock(Initializer.class, "init1");
		Initializer init2 = mock(Initializer.class, "init2");

		when(init1.getComponentClass()).thenReturn((Class) Integer.class);
		when(init1.getComponentClass()).thenReturn((Class) Float.class);
		when(init1.isBefore(init2)).thenReturn(true);
		when(init2.isBefore(init1)).thenReturn(true);
		doAnswer(new Fail()).when(init1).run();
		doAnswer(new Fail()).when(init2).run();

		engine.runInitializers(Arrays.asList(init1, init2));
	}

	@Test
	public void testCreateInitializersFromComponentProvider() throws Exception {
		Initializer init1 = mock(Initializer.class);
		InitializerProvider provider = mock(InitializerProvider.class);
		when(provider.getInitializers()).thenReturn(Arrays.asList(init1));
		Iterable<Initializer> initializers = engine
				.createInitializersFromComponent(provider);
		assertEquals(1, Iterables.size(initializers));
	}

	private static class TestProvider {
		public boolean initialized1;
		public boolean initialized2;

		@LafInitializer
		public void init1() {
			initialized1 = true;
		}

		@LafInitializer(afterRef = @InitializerRef(componentClass = TestProvider.class, id = "init1"))
		public void init2() {
			initialized2 = true;
		}
	}

	@Test
	public void testCreateInitializersFromComponentMethod() throws Exception {
		TestProvider provider = new TestProvider();
		Iterable<Initializer> initializers = engine
				.createInitializersFromComponent(provider);

		assertEquals(2, Iterables.size(initializers));
		Initializer init1 = null;
		Initializer init2 = null;

		for (Initializer initializer : initializers) {
			if (initializer.getId().equals("init1")) {
				assertNull(init1);
				init1 = initializer;
			} else if (initializer.getId().equals("init2")) {
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

		assertTrue(provider.initialized1);
		assertTrue(provider.initialized2);

	}

	@Test
	public void testCheckUnique() throws Exception {
		Initializer init1 = mock(Initializer.class);
		Initializer init2 = mock(Initializer.class);
		assertTrue(engine.checkUnique(Arrays.asList(init1)));
		assertTrue(engine.checkUnique(Arrays.asList(init1, init2)));
		assertFalse(engine.checkUnique(Arrays.asList(init1, init1)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckUniqueIds() throws Exception {
		Initializer init1 = mock(Initializer.class);
		Initializer init2 = mock(Initializer.class);
		when(init1.getComponentClass()).thenReturn((Class) Integer.class);
		when(init2.getComponentClass()).thenReturn((Class) Float.class);

		// same id, different classes
		when(init1.getId()).thenReturn("a");
		when(init2.getId()).thenReturn("a");
		assertTrue(engine.checkUniqueIds(Arrays.asList(init1, init2)));

		// same id, same classes
		when(init2.getComponentClass()).thenReturn((Class) Integer.class);
		assertFalse(engine.checkUniqueIds(Arrays.asList(init1, init2)));

		// different id, same classes
		when(init2.getId()).thenReturn("b");
		assertTrue(engine.checkUniqueIds(Arrays.asList(init1, init2)));
	}

	@Test
	public void testCalculateBeforeRelation() throws Exception {
		Initializer init1 = mock(Initializer.class);
		Initializer init2 = mock(Initializer.class);
		when(init1.isBefore(init2)).thenReturn(true);

		Map<Initializer, Set<Initializer>> map = engine
				.calculateBeforeRelation(Arrays.asList(init1, init2));
		assertEquals(2, map.size());
		assertTrue(map.get(init2).isEmpty());
		assertEquals(1, map.get(init1).size());
		assertTrue(map.get(init1).contains(init2));
	}
}
