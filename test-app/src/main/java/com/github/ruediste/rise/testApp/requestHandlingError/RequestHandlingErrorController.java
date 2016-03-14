package com.github.ruediste.rise.testApp.requestHandlingError;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;

public class RequestHandlingErrorController extends ControllerMvc<RequestHandlingErrorController> {

    @UrlUnsigned
    public ActionResult index() {
        throw new RuntimeException("Boom!");
    }
}
