package laf.skeleton.sample;

import laf.core.base.ActionResult;
import laf.core.web.annotation.ActionPath;
import laf.skeleton.base.MvcControllerBase;

public class SampleMvcController extends MvcControllerBase {

	@ActionPath(value = "/", primary = true)
	public ActionResult index() {
		return util.view(SampleMvcView.class, null);
	}
}
