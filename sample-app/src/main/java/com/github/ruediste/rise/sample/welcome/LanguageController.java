package com.github.ruediste.rise.sample.welcome;

import java.util.Locale;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste.rise.core.web.RedirectToRefererRenderResult;

/**
 * Controller to switch the current language.
 */
public class LanguageController extends ControllerMvc<LanguageController> {

    @Inject
    CurrentLocale currentLocale;

    public ActionResult switchLanguage(String newLanguage) {
        currentLocale.setCurrentLocale(Locale.forLanguageTag(newLanguage));
        return new RedirectToRefererRenderResult();
    }

}
