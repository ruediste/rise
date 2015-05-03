package com.github.ruediste.laf.sample.welcome;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.ActionPath;

public class WelcomeController extends ControllerMvcWeb<WelcomeController> {

	static class Data {

	}

	@ActionPath(value = "/", primary = true)
	public ActionResult index() {
		return view(WelcomeView.class, new Data());
	}

	public ActionResult other() {
		return view(OtherView.class, "Test");
	}
}
