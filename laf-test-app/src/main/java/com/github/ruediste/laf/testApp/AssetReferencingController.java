package com.github.ruediste.laf.testApp;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.core.ActionResult;

public class AssetReferencingController extends ControllerMvcWeb {

	public ActionResult index() {
		return view(AssetReferencingView.class, "Hello");
	}
}
