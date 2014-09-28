package laf.testApp;

import javax.inject.Inject;

import laf.core.base.ActionResult;
import laf.mvc.core.api.MController;
import laf.mvc.web.MWControllerUtil;

@MController
public class TestIndexController {

	@Inject
	MWControllerUtil util;

	public ActionResult index() {
		return util.view(TestIndexView.class, null);
	}
}
