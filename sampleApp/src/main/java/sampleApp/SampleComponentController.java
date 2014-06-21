package sampleApp;

import laf.base.ActionResult;
import laf.component.core.ComponentController;

@ComponentController
public class SampleComponentController {

	public ActionResult index() {
		return null;
	}

	public String getSampleText() {
		return "Hello World";
	}
}
