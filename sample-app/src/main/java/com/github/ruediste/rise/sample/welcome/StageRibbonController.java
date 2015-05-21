package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.integration.StageRibbonControllerBase;
import com.github.ruediste.rise.sample.SampleBundle;

public class StageRibbonController extends
		StageRibbonControllerBase<StageRibbonController> {

	@Inject
	SampleBundle bundle;

	@Override
	protected AssetBundleOutput getAssets() {
		return bundle.out;
	}

}
