package com.github.ruediste.laf.core.web.assetPipeline;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.laf.core.PathInfoIndex;

@RunWith(MockitoJUnitRunner.class)
public class AssetRequestMapperTest {

	@Mock
	PathInfoIndex index;

	@InjectMocks
	AssetRequestMapper mapper;

	AssetPipelineConfiguration pipelineConfig;

	@Before
	public void setup() {
		pipelineConfig = new AssetPipelineConfiguration();
		mapper.pipelineConfiguration = pipelineConfig;
	}

	private static class A extends AssetBundle {
		AssetBundleOutput out = new AssetBundleOutput(this);
		private Asset[] assets;

		A(Asset... assets) {
			this.assets = assets;

		}

		@Override
		public void initialize() {
			Arrays.stream(assets).forEach(out);
		}
	}

	@Test
	public void testRegisterAssets() throws Exception {
		mapper.registerAssets(Arrays
				.asList(new A(new TestAsset("/foo", "bar"))));
		verify(index).registerPathInfo(eq("/assets/foo"), any());
	}

	@Test
	public void testRegisterAssetsTwoSame() throws Exception {
		mapper.registerAssets(Arrays.asList(new A(new TestAsset("/foo", "bar"),
				new TestAsset("/foo", "bar"))));
		verify(index).registerPathInfo(eq("/assets/foo"), any());
	}

	@Test(expected = RuntimeException.class)
	public void testRegisterAssetsTwoDifferent() throws Exception {
		mapper.registerAssets(Arrays.asList(new A(new TestAsset("/foo", "bar"),
				new TestAsset("/foo", "barr"))));
		fail();
	}

}
