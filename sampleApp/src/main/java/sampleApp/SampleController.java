package sampleApp;

import laf.base.*;

@Controller
public class SampleController {

	public ActionResult index() {
		return new NormalRenderResult(
				"<html><head></head><body>Hello World</body></html>");
	}

}
