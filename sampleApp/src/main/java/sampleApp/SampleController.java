package sampleApp;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import laf.base.ActionResult;
import laf.mvc.Controller;
import laf.mvc.MvcControllerUtil;

@Controller
public class SampleController {

	@Inject
	UserTransaction transaction;

	@Inject
	MvcControllerUtil util;

	public ActionResult index() {
		return util.view(SampleView.class, "Hello World");
	}

	public ActionResult add() throws Exception {
		transaction.commit();
		return null;
	}
}
