package laf.testApp.smokeTest;

import javax.inject.Inject;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.mvc.core.api.MController;
import com.github.ruediste.laf.mvc.web.MWControllerUtil;

@MController
public class SmokeTestController {

	@Inject
	MWControllerUtil util;

	public ActionResult index() {
		return util.view(SmokeTestView.class, "Smoke Passed");
	}
}
