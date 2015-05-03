package com.github.ruediste.laf.sample.welcome;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.api.ControllerMvcWeb;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.ActionPath;

public class WelcomeController extends ControllerMvcWeb<WelcomeController> {

	@Inject
	Logger log;

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
