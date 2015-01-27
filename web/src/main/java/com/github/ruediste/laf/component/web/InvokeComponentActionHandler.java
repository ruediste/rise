package com.github.ruediste.laf.component.web;

import com.github.ruediste.laf.component.core.ComponentActionRequest;
import com.github.ruediste.laf.component.core.RequestHandler;
import com.github.ruediste.laf.core.base.ActionResult;

public class InvokeComponentActionHandler implements
		RequestHandler<ComponentActionRequest> {

	@Override
	public ActionResult handle(ComponentActionRequest request) {
		throw new RuntimeException("Not Implemented");
	}

}
