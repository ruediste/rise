package sampleApp;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.base.ComponentController;
import laf.component.PageActionResult;

@ComponentController
public class SampleComponentController {
	@Inject
	Instance<PageActionResult> pageRenderResult;

	public ActionResult index() {
		return pageRenderResult.get();
	}

	public String getSampleText() {
		return "Hello World";
	}
}
