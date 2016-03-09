package com.github.ruediste.rise.sample.front;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.core.web.HttpServletResponseCustomizer;
import com.github.ruediste.rise.core.web.RedirectToRefererRenderResult;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.sample.PageView;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.welcome.WelcomeController;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.base.Throwables;

public class ReqestErrorController extends ControllerMvc<ReqestErrorController>
        implements HttpServletResponseCustomizer {

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
    @Label("Unexpected Error")
    private static class View extends PageView<ReqestErrorController, Data>
            implements HttpServletResponseCustomizer {

        @Override
        public void renderContent(SampleCanvas html) {
            // @formatter:off
			html.bContainer()
			    .bRow()
			        .bCol(x->x.xs(12))
						.h1().content("Unexpected Error Occured")
					._bCol()
				._bRow()
				.div().CLASS("jumbotron")
				    .bRow()
				        .bCol(x->x.xs(12))
						    .form().BformHorizontal()
						        .fieldset()
						            .legend()
						                .content("Open support ticket")
						            .bFormGroup()
						                .bControlLabel(x->x.sm(2)).FOR("message").content("What were you doing?")
						                .bCol(x->x.sm(10))
						                    .textarea().BformControl().ID("message").NAME("message").STYLE("width:100%;").ROWS("10")
						                        .content("I tried to ...")
						                 ._bCol()
						            ._bFormGroup();
						            html.bFormGroup()
						                .bCol(x->x.sm(10).smOffset(2))
						                    .bButton(x->x.success()).ID("createTicket").NAME("createTicket")
						                        .content("Create Ticket")
				                        ._bCol()
						            ._bFormGroup()
						        ._fieldset()
						    ._form()
						._bCol()
					._bRow()
				._div();
				html.bRow()
				    .bCol(x->x.xs(12))
				        .bButtonA(x->x.primary()).HREF(go(WelcomeController.class).index()).content("Go to Start Page")
				        .fIf(data().stage==ApplicationStage.DEVELOPMENT, () ->html.
				            bButtonA(x->x.danger()).HREF(go().dropAndCreateDataBase()).content("Drop-And-Create Database"))
    				    ._bCol()
				._bRow();
			    if (data().stage!=null && data().stage!=ApplicationStage.PRODUCTION){
    				html.bRow()
    				    .bCol(x->x.xs(12))
                            .pre().content(data().message)
                        ._bCol()
                    ._bRow();
			    }
			   
			html._bContainer();
			// @formatter:on
        }

        @Override
        public void customizeServletResponse(HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

    @Updating
    public ActionResult dropAndCreateDataBase() {
        config.recreateDatabases();
        return new RedirectToRefererRenderResult();
    }

    @Override
    public void customizeServletResponse(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
