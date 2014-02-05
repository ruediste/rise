package laf;

import javax.inject.Inject;

public class SampleController {

	@Inject
	ActionContext actionContext;

	public String index() {
		actionContext.setRenderResult(new NormalRenderResult(
				"<html><head></head><body>Hello World</body></html>"));
		return null;
	}

}
