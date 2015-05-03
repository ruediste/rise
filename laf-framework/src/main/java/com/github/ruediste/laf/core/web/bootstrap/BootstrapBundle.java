package com.github.ruediste.laf.core.web.bootstrap;

import com.github.ruediste.laf.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;

public class BootstrapBundle extends AssetBundle {

	public AssetBundleOutput out = new AssetBundleOutput(this);
	public AssetBundleOutput theme = new AssetBundleOutput(this);

	private AssetBundleOutput fontEot = new AssetBundleOutput(this);
	private AssetBundleOutput fontSvg = new AssetBundleOutput(this);
	private AssetBundleOutput fontTtf = new AssetBundleOutput(this);
	private AssetBundleOutput fontWoff = new AssetBundleOutput(this);
	private AssetBundleOutput fontWoff2 = new AssetBundleOutput(this);

	@Override
	public void initialize() {
		paths("./css/bootstrap.css").insertMinInProd().load().send(out);
		paths("./js/jquery-2.1.3.js", "./js/bootstrap.js").insertMinInProd()
				.load().send(out);
		paths("./css/bootstrap-theme.css").insertMinInProd().load().send(theme);
		sendFont("eot", fontEot);
		sendFont("svg", fontSvg);
		sendFont("ttf", fontTtf);
		sendFont("woff", fontWoff);
		sendFont("woff2", fontWoff2);
	}

	private void sendFont(String ext, AssetBundleOutput out) {
		paths("./fonts/glyphicons-halflings-regular." + ext).load().send(out);
	}
}
