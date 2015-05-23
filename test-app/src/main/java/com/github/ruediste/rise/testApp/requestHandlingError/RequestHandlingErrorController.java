package com.github.ruediste.rise.testApp.requestHandlingError;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;

public class RequestHandlingErrorController extends
		ControllerMvc<RequestHandlingErrorController> {

	public ActionResult index() {
		throw new RuntimeException("Boom!");
	}
}
