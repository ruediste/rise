package sampleApp;

import laf.base.ActionResult;
import laf.base.ComponentController;
import laf.component.PageRenderResult;

@ComponentController
public class SampleComponentController {

	public ActionResult index() {
		return PageRenderResult.create(this, SampleComponentView.class);
	}

	public String getSampleText() {
		return "Hello World";
	}
}
