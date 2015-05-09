package com.github.ruediste.rise.testApp;

import com.github.ruediste.rise.api.ControllerMvcWeb;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.web.ActionPath;

public class AssetReferencingController extends
		ControllerMvcWeb<AssetReferencingController> {

	@ActionPath(value = "/", primary = true)
	public ActionResult index() {
		return view(AssetReferencingView.class, "Hello");
	}
}
