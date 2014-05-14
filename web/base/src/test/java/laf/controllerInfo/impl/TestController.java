package laf.controllerInfo.impl;

import laf.base.ActionResult;
import laf.base.Controller;

@Controller
public class TestController {

	public void nonActionMethod() {
	}

	public ActionResult actionMethod(int arg) {
		return null;
	}

	public EmbeddedTestController actionMethodEmbedded() {
		return null;
	}
}
