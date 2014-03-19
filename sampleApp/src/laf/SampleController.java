package laf;

import javax.inject.Inject;

import laf.urlMapping.UrlMapping;

@Controller
public class SampleController {

	@Inject
	UrlMapping mapping;

	public ActionResult index() {
		return new NormalRenderResult(
				"<html><head></head><body>Hello World</body></html>");
	}

}
