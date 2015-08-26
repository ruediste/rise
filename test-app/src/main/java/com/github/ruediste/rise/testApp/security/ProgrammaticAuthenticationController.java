package com.github.ruediste.rise.testApp.security;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.AuthenticationHolder;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.TestPageTemplate;
import com.github.ruediste.rise.testApp.TestPageTemplate.TestPageTemplateParameters;
import com.github.ruediste.rise.testApp.ViewMvc;
import com.github.ruediste1.i18n.lString.LString;

public class ProgrammaticAuthenticationController extends
        ControllerMvc<ProgrammaticAuthenticationController> {

    @Inject
    CoreRequestInfo info;

    public static class View extends
            ViewMvc<ProgrammaticAuthenticationController, String> {

        @Inject
        TestPageTemplate template;

        @Override
        protected void render(TestCanvas html) {
            template.renderOn(html, new TestPageTemplateParameters() {

                @Override
                public void renderContent(TestCanvas html) {
                    html.write(data());

                }

                @Override
                public LString getTitle() {
                    return locale -> "Test App";
                }
            });
        }

    }

    @Inject
    AuthenticationHolder authenticationHolder;

    public ActionResult noAuthenticationRequired() {
        return view(View.class, "success");
    }

    public ActionResult authenticationRequired() {
        authenticationHolder.checkAutheticationPresetn();
        return view(View.class, "success");
    }

}
