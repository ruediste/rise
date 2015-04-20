package com.github.ruediste.laf.sample;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.ActionPath;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;

public class WelcomeController implements IControllerMvcWeb {

	@ActionPath(value = "/", primary = true)
	public ActionResult index() {
		return null;
	}
}
