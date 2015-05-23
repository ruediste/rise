package com.github.ruediste.rise.sample.front;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.sample.db.PageView;
import com.github.ruediste.rise.sample.welcome.WelcomeController;

public class ReqestErrorController extends ControllerMvc<ReqestErrorController> {

    @Inject
    CoreRequestInfo info;

    private static class Data {

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
				.div(class_("col-xs-12 col-md-4"))
					.a(class_("btn btn-primary").href(url(go(WelcomeController.class).index()))).content("Go to Start Page")
				._div()
			._div();
			// @formatter:on
        }
    }

    public ActionResult index() {
        Data data = new Data();
        return view(View.class, data);
    }
}
