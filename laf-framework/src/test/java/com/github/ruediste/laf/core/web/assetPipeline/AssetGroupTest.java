package com.github.ruediste.laf.core.web.assetPipeline;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;

@RunWith(MockitoJUnitRunner.class)
public class AssetGroupTest {

	Asset asset;

	AssetBundle bundle;

	private AssetGroup group;

	@Before
	public void setup() throws UnsupportedEncodingException {
		bundle = new AssetBundle() {
		};

		asset = spy(new TestAsset("foo", "Hello"));
		group = new AssetGroup(bundle, Arrays.asList(asset));
	}

	@Test
	public void testCache() {

		AssetGroup cached = group.cache();

		checkAccessCount(0);

		cached.assets.forEach(this::access);
		checkAccessCount(1);

		cached.assets.forEach(this::access);
		checkAccessCount(1);

		bundle.clearCache();

		cached.assets.forEach(this::access);
		checkAccessCount(2);
	}

	private void checkAccessCount(int count) {
		verify(asset, times(count)).getName();
		verify(asset, times(count)).getData();
	}

	private void access(Asset r) {
		r.getName();
		r.getData();
	}

	@Test
	public void testNameHashCachingMiddle() throws Exception {

		// test with hash in the middle
		AssetGroup name = group.name("{name}/{hash}");

		checkAccessCount(0);
		name.assets.forEach(this::access);

		checkAccessCount(1);
		name.assets.forEach(this::access);
		checkAccessCount(1);
	}

	@Test
	public void testNameHashCachingBeginning() throws Exception {

		// test with hash in the middle
		AssetGroup name = group.name("{hash}/{name}");

		checkAccessCount(0);
		name.assets.forEach(this::access);

		checkAccessCount(1);
		name.assets.forEach(this::access);
		checkAccessCount(1);
	}

	private byte[] byteArray(String s) throws UnsupportedEncodingException {
		return s.getBytes("UTF-8");
	}

	static class TestAsset implements Asset {

		private String name;
		private String data;

		public TestAsset(String name, String data) {
			this.name = name;
			this.data = data;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public AssetType getAssetType() {
			return DefaultAssetTypes.CSS;
		}

		@Override
		public String getContentType() {
			return "text/css";
		}

		@Override
		public byte[] getData() {
			return data.getBytes(Charsets.UTF_8);
		}

	}

	@Test
	public void testCombine() throws Exception {
		Asset r1 = new TestAsset("foo1", "bar1");
		Asset r2 = new TestAsset("foo2", "bar2");
		group = new AssetGroup(bundle, Arrays.asList(r1, r2));
		AssetGroup collect = group.combine("foo3");
		assertEquals(1, collect.assets.size());
		Asset r = Iterables.getOnlyElement(collect.assets);
		assertArrayEquals(byteArray("bar1bar2"), r.getData());
	}

	@Test
	public void testForkJoin() throws Exception {
		Set<String> names = group.forkJoin(x -> x.name("foo"),
				x -> x.name("bar")).assets.stream().map(x -> x.getName())
				.collect(toSet());
		assertThat(names, containsInAnyOrder("foo", "bar"));
	}

}
