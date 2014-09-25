package laf.core.web.resource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Iterables;

@RunWith(MockitoJUnitRunner.class)
public class ResourceGroupTest {

	@Mock
	DataEqualityTracker dataEqualityTracker;

	@Spy
	TestResourceImpl resource = new TestResourceImpl("foo", "Hello");

	ResourceBundle bundle;

	private ResourceGroup group;

	@Before
	public void setup() throws UnsupportedEncodingException {
		bundle = new ResourceBundle() {
			@Override
			protected void initializeImpl() {
			}
		};

		group = new ResourceGroup(bundle, Arrays.asList(resource));
	}

	@Test
	public void testCache() {

		ResourceGroup cached = group.cache();

		checkAccessCount(0);

		cached.resources.forEach(this::access);
		checkAccessCount(1);

		cached.resources.forEach(this::access);
		checkAccessCount(1);

		bundle.clearCache();

		cached.resources.forEach(this::access);
		checkAccessCount(2);

		assertTrue(Iterables
				.getOnlyElement(cached.resources)
				.getDataEqualityTracker()
				.containsSameDataAs(
						Iterables.getOnlyElement(group.resources)
								.getDataEqualityTracker()));
	}

	private void checkAccessCount(int count) {
		verify(resource, times(count)).getName();
		verify(resource, times(count)).getData();
	}

	private void access(Resource r) {
		r.getName();
		r.getData();
	}

	@Test
	public void testNameHashCachingMiddle() throws Exception {

		// test with hash in the middle
		ResourceGroup name = group.name("{name}/{hash}");

		checkAccessCount(0);
		name.resources.forEach(this::access);

		checkAccessCount(1);
		name.resources.forEach(this::access);
		checkAccessCount(1);
	}

	@Test
	public void testNameHashCachingBeginning() throws Exception {

		// test with hash in the middle
		ResourceGroup name = group.name("{hash}/{name}");

		checkAccessCount(0);
		name.resources.forEach(this::access);

		checkAccessCount(1);
		name.resources.forEach(this::access);
		checkAccessCount(1);
	}

	private byte[] byteArray(String s) throws UnsupportedEncodingException {
		return s.getBytes("UTF-8");
	}

	@Test
	public void testCollect() throws Exception {
		Resource r1 = new TestResourceImpl("foo1", "bar1");
		Resource r2 = new TestResourceImpl("foo2", "bar2");
		group = new ResourceGroup(bundle, Arrays.asList(r1, r2));
		ResourceGroup collect = group.collect("foo3");
		assertEquals(1, collect.resources.size());
		Resource r = Iterables.getOnlyElement(collect.resources);
		assertArrayEquals(byteArray("bar1bar2"), r.getData());

		assertTrue(r.getDataEqualityTracker().containsSameDataAs(
				Iterables.getOnlyElement(group.collect("foo4").resources)
						.getDataEqualityTracker()));
	}
}
