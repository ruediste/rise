package com.github.ruediste.rise.testApp.app;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.ViewMvc;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.base.Throwables;

public class RequestErrorHandlerController
        extends ControllerMvc<RequestErrorHandlerController> {
    @Inject
    Logger log;

    @Inject
    CoreRequestInfo info;

    @Inject
    ApplicationStage stage;

    @Inject
    DataBaseLinkRegistry registry;

    @Inject
    CoreConfiguration config;

    private static class Data {
        ApplicationStage stage;
        String message;
    }

    @Labeled
    private static class View
            extends ViewMvc<RequestErrorHandlerController, Data>
            implements HttpServletResponseCustomizer {

        @Override
        protected void renderContent(TestCanvas html) {
            html.h1().content("An Unexpected error occurred").pre()
                    .content(data().message);
        }

        @Override
        public void customizeServletResponse(HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @UrlUnsigned
    public ActionResult index() {
        Data data = new Data();
        data.stage = stage;
        Throwable requestError = info.getRequestError();
        if (requestError == null)
            data.message = "No Error available. Don't reload the error page!";
        else
            data.message = Throwables.getStackTraceAsString(requestError);
        return view(View.class, data);
    }
}
