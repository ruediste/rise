package laf.testApp.smokeTest;

import javax.inject.Inject;

import laf.core.base.ActionResult;
import laf.mvc.core.api.MController;
import laf.mvc.web.MWControllerUtil;

@MController
public class SmokeTestController {

	@Inject
	MWControllerUtil util;

	public ActionResult index() {
		return util.view(SmokeTestView.class, "Smoke Passed");
	}
}
