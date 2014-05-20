package sampleApp;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.base.ComponentController;
import laf.component.PageRenderResult;

@ComponentController
public class SampleComponentController {
	@Inject
	Instance<PageRenderResult> pageRenderResult;

	public ActionResult index() {
		return pageRenderResult.get();
	}

	public String getSampleText() {
		return "Hello World";
	}
}
