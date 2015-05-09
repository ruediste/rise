package com.github.ruediste.rise.testApp;

import com.github.ruediste.rise.api.ControllerMvcWeb;
import com.github.ruediste.rise.core.ActionResult;

public class AssetReferencingController extends ControllerMvcWeb {

	public ActionResult index() {
		return view(AssetReferencingView.class, "Hello");
	}
}
