package com.github.ruediste.laf.test;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.core.ActionResult;

public class SimpleMvcController extends ControllerMvcWeb {

	public ActionResult index() {
		return view(SimpleMvcView.class, "Hello");
	}
}
