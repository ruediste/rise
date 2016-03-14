package com.github.ruediste.rise.core.web.assetPipeline;

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
import org.slf4j.Logger;

import com.github.ruediste.rise.core.PathInfoIndex;

@RunWith(MockitoJUnitRunner.class)
public class AssetRequestMapperTest {

    @Mock
    PathInfoIndex index;

    @Mock
    Logger log;

    @InjectMocks
    AssetHelper helper;

    @InjectMocks
    AssetRequestMapper mapper;

    AssetPipelineConfiguration pipelineConfig;

    @Before
    public void setup() {
        pipelineConfig = new AssetPipelineConfiguration();
        mapper.helper = helper;
        mapper.pipelineConfiguration = pipelineConfig;
        helper.pipelineConfiguration = pipelineConfig;
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
        A a = new A(new TestAsset("foo", "bar"));
        a.initialize();
        mapper.registerAssets(Arrays.asList(a));
        verify(index).registerPathInfo(eq("/assets/foo"), any());
    }

    @Test
    public void testRegisterAssetsTwoSame() throws Exception {
        A a = new A(new TestAsset("/foo", "bar"), new TestAsset("foo", "bar"));
        a.initialize();
        mapper.registerAssets(Arrays.asList(a));
        verify(index).registerPathInfo(eq("/assets/foo"), any());
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterAssetsTwoDifferent() throws Exception {
        A a = new A(new TestAsset("/foo", "bar"), new TestAsset("/foo", "barr"));
        a.initialize();
        mapper.registerAssets(Arrays.asList(a));
        fail();
    }

}
