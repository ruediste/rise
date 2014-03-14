package laf;

import javax.inject.Inject;

@Controller
public class SampleController {


	public ActionResult index() {
		return new NormalRenderResult(
				"<html><head></head><body>Hello World</body></html>");
	}

}
