package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.core.web.ActionPath;
import com.github.ruediste1.i18n.label.Label;

public class WelcomeController extends ControllerMvc<WelcomeController> {

    @Inject
    Logger log;

    static class Data {

    }

    @UrlUnsigned
    @Label("Home")
    @ActionPath(value = "/", primary = true)
    public ActionResult index() {
        return view(WelcomeView.class, new Data());
    }

    public ActionResult error() {
        throw new RuntimeException("BOOM");
    }
}
