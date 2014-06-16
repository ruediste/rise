package sampleApp;

import laf.base.ActionResult;
import laf.http.NormalRenderResult;
import laf.mvc.Controller;

@Controller
public class SampleController {

	public ActionResult index() {
		return new NormalRenderResult(
				"<html><head></head><body>Hello World</body></html>");
	}

}
