package laf.initialization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Iterables;

public class InitializationServiceTest {

	private final class Fail implements Answer<Object> {
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			Assert.fail();
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
		Initializer init1 = Mockito.mock(Initializer.class, "init1");
		Initializer init2 = Mockito.mock(Initializer.class, "init2");

		Mockito.when(init1.getRepresentingClass()).thenReturn(
				(Class) Integer.class);
		Mockito.when(init2.getRepresentingClass()).thenReturn(
				(Class) Float.class);
		Mockito.when(init1.getDeclaredRelations(init2)).thenReturn(
				Arrays.asList(new InitializerDependsRelation(init1, init2,
						false)));
		Mockito.when(init1.getRelatedRepresentingClasses()).thenReturn(
				new HashSet<>(Arrays.<Class<?>> asList(Float.class)));
		service.runInitializers(init1,
				new HashSet<>(Arrays.asList(init1, init2)));

		InOrder order = Mockito.inOrder(init1, init2);
		order.verify(init1).run();
		order.verify(init2).run();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = RuntimeException.class)
	public void testRunInitializersLoop() throws Exception {
		Initializer init1 = Mockito.mock(Initializer.class, "init1");
		Initializer init2 = Mockito.mock(Initializer.class, "init2");

		Mockito.when(init1.getRepresentingClass()).thenReturn(
				(Class) Integer.class);
		Mockito.when(init1.getRepresentingClass()).thenReturn(
				(Class) Float.class);
		Mockito.when(init1.getDeclaredRelations(init2)).thenReturn(
				Arrays.asList(new InitializerDependsRelation(init1, init2,
						false), new InitializerDependsRelation(init2, init1,
						false)));
		Mockito.doAnswer(new Fail()).when(init1).run();
		Mockito.doAnswer(new Fail()).when(init2).run();

		service.runInitializers(init1,
				new HashSet<>(Arrays.asList(init1, init2)));
	}

	@Test
	public void testCreateInitializersProvider() throws Exception {
		Initializer init1 = Mockito.mock(Initializer.class);
		InitializerProvider provider = Mockito.mock(InitializerProvider.class);
		Mockito.when(provider.getInitializers()).thenReturn(
				Arrays.asList(init1));
		Iterable<Initializer> initializers = service
				.createInitializers(provider);
		Assert.assertEquals(1, Iterables.size(initializers));
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

		Assert.assertEquals(2, Iterables.size(initializers));
		Initializer init1 = null;
		Initializer init2 = null;

		for (Initializer initializer : initializers) {
			if (initializer.getRepresentingClass().equals(
					TestInitializer1.class)) {
				Assert.assertNull(init1);
				init1 = initializer;
			} else if (initializer.getRepresentingClass().equals(
					TestInitializer2.class)) {
				Assert.assertNull(init2);
				init2 = initializer;
			} else {
				Assert.fail("unexpected initializer");
			}
		}

		Assert.assertFalse(init2.isBefore(init1));
		Assert.assertFalse(init1.isAfter(init2));
		Assert.assertTrue(init1.isBefore(init2));
		Assert.assertTrue(init2.isAfter(init1));

		init1.run();
		init2.run();

		Assert.assertTrue(provider1.initialized);
		Assert.assertTrue(provider2.initialized);

	}

	@Test
	public void testCheckUnique() throws Exception {
		Initializer init1 = Mockito.mock(Initializer.class);
		Initializer init2 = Mockito.mock(Initializer.class);
		Assert.assertTrue(service.checkUnique(Arrays.asList(init1)));
		Assert.assertTrue(service.checkUnique(Arrays.asList(init1, init2)));
		Assert.assertFalse(service.checkUnique(Arrays.asList(init1, init1)));
	}

	@Test
	public void testCalculateBeforeRelation() throws Exception {
		Initializer init1 = Mockito.mock(Initializer.class);
		Initializer init2 = Mockito.mock(Initializer.class);
		Mockito.when(init1.isBefore(init2)).thenReturn(true);

		Map<Initializer, Set<Initializer>> map = service
				.calculateBeforeRelation(Arrays.asList(init1, init2));
		Assert.assertEquals(2, map.size());
		Assert.assertTrue(map.get(init2).isEmpty());
		Assert.assertEquals(1, map.get(init1).size());
		Assert.assertTrue(map.get(init1).contains(init2));
	}
}
