package laf.controllerInfo.impl;

import laf.core.base.ActionResult;
import laf.mvc.core.api.MController;

@MController
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
