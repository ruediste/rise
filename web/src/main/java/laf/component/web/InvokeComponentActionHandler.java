package laf.component.web;

import laf.component.core.ComponentActionRequest;
import laf.component.core.RequestHandler;
import laf.core.base.ActionResult;

public class InvokeComponentActionHandler implements
		RequestHandler<ComponentActionRequest> {

	@Override
	public ActionResult handle(ComponentActionRequest request) {
		throw new RuntimeException("Not Implemented");
	}

}
