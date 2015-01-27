package sampleApp;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.mvc.core.api.MController;
import com.github.ruediste.laf.mvc.web.MWControllerUtil;

@MController
public class SampleController {

	@Inject
	UserTransaction transaction;

	@Inject
	MWControllerUtil util;

	@Inject
	Repo repo;

	public ActionResult index() {
		return util.view(SampleView.class, repo.getUser());
	}

	public ActionResult add() throws Exception {
		transaction.commit();
		return null;
	}
}
