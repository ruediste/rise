package sampleApp;

import laf.base.ActionResult;
import laf.base.Controller;
import laf.component.PageRenderResult;

@Controller
public class SampleComponentController {

	public ActionResult index() {
		return new PageRenderResult<>(new SamplePage(new SampleView()));
	}

	public String getSampleText() {
		return "Hello World";
	}
}
