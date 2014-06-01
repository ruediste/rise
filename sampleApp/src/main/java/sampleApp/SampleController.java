package sampleApp;

import laf.base.*;
import laf.http.NormalRenderResult;

@Controller
public class SampleController {

	public ActionResult index() {
		return new NormalRenderResult(
				"<html><head></head><body>Hello World</body></html>");
	}

}
