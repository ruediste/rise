package sampleApp;

import laf.component.core.api.CController;
import laf.core.base.ActionResult;

@CController
public class SampleComponentController {

	public ActionResult index() {
		return null;
	}

	public String getSampleText() {
		return "Hello World";
	}
}
