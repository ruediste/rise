package com.github.ruediste.rise.testApp.mvc;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;

public class TestMvcController extends ControllerMvc<TestMvcController> {

    @UrlUnsigned
    public ActionResult noArgs() {
        return null;
    }

    @UrlUnsigned
    public ActionResult withInt(int i) {
        return null;
    }

    @UrlUnsigned
    public ActionResult withIntLong(int i, Long l) {
        return null;
    }

    @UrlUnsigned
    public ActionResult withString(String s) {
        return null;
    }
}