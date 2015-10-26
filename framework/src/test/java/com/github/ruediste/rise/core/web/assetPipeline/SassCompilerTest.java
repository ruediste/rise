package com.github.ruediste.rise.core.web.assetPipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SassCompilerTest {

    @Test
    public void test() {
        SassCompiler compiler = new SassCompiler();
        AssetHelper helper = new AssetHelper();
        AssetPipelineConfiguration config = new AssetPipelineConfiguration();
        compiler.helper = helper;
        helper.pipelineConfiguration = config;

        TestAsset sass = new TestAsset("foo.scss", "body { color: red;}");
        sass.location = "assets/res/sass.scss";
        Asset css = compiler.create("{name}.{extT}").apply(sass);
        assertEquals("body{color:red}\n", css.getDataString());
    }
}
