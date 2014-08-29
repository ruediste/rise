package laf.component.web.requestProcessing;

import laf.component.core.RequestHandler;
import laf.component.core.reqestProcessing.ComponentActionRequest;
import laf.core.base.ActionResult;

public class InvokeComponentActionHandler implements
		RequestHandler<ComponentActionRequest> {

	@Override
	public ActionResult handle(ComponentActionRequest request) {
		throw new RuntimeException("Not Implemented");
	}

}
