package com.github.ruediste.rise.sample.front;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.RedirectToRefererRenderResult;
import com.github.ruediste.rise.mvc.Updating;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.sample.db.PageView;
import com.github.ruediste.rise.sample.welcome.WelcomeController;
import com.google.common.base.Throwables;

public class ReqestErrorController extends ControllerMvc<ReqestErrorController> {

    @Inject
    Logger log;

    @Inject
    CoreRequestInfo info;

    @Inject
    ApplicationStage stage;

    @Inject
    private DataBaseLinkRegistry registry;

    private static class Data {
        ApplicationStage stage;
        String message;
    }

    private static class View extends PageView<ReqestErrorController, Data> {

        @Override
        protected void renderBody(HtmlCanvas html) throws IOException {
            // @formatter:off
			html
			.nav(class_("navbar navbar-inverse navbar-fixed-top"))._nav()
			.div(class_("container"))
				.div(class_("row"))
					.div(class_("col-xs-12"))
						.h1().content("Unexpected Error Occured")
					._div()
				._div()
				.div(class_("jumbotron"))
					.div(class_("row"))
						.div(class_("col-xs-12"))
						    .form(class_("form-horizontal"))
						        .fieldset()
						            .legend()
						                .content("Open support ticket")
						            .div(class_("control-group"))
						                .label(class_("control-label").for_("message"))
						                    .content("What were you doing?")
						                .div(class_("controls"))
						                    .textarea(id("message").name("message").style("width:100%;").rows("10"))
						                        .content("I tried to ...")
						                ._div()
						            ._div()
						            .div(class_("control-group"))
						                .div(class_("controls"))
						                    .button(id("createTicket").name("createTicket").class_("btn btn-success"))
						                        .content("Create Ticket")
						                ._div()
						            ._div()
						        ._fieldset()
						    ._form()
						._div()
					._div()
				._div()
				.div(class_("row"))
    				.div(class_("col-xs-12"))
    					.a(class_("btn btn-primary").href(url(go(WelcomeController.class).index()))).content("Go to Start Page")
    				._div()
				._div();
			    if (data().stage!=null && data().stage!=ApplicationStage.PRODUCTION){
    				html.div(class_("row"))
                        .div(class_("col-xs-12"))
                            .pre().content(data().message)
                        ._div()
                    ._div();
			    }
			    if (data().stage!=null && data().stage==ApplicationStage.DEVELOPMENT){
			        html.div(class_("row"))
    			        .div(class_("col-xs-12"))
    			            .a(class_("btn btn-danger").href(url(go().dropAndCreateDataBase()))).content("Drop-And-Create Database")
    			        ._div()
			        ._div();
			    }
			html._div();
			// @formatter:on
        }
    }

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
        log.info("Dropping and Creating DB schemas ...");
        registry.dropAndCreateSchemas();
        commit();
        return new RedirectToRefererRenderResult();
    }

}
