package laf.skeleton.sample;

import laf.core.base.ActionResult;
import laf.skeleton.base.MvcControllerBase;

public class SampleMvcController extends MvcControllerBase {

	public ActionResult index() {
		return util.view(SampleMvcView.class, null);
	}
}
