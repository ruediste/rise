package com.github.ruediste.rise.sample.welcome;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.integration.PageRenderer;
import com.github.ruediste.rise.nonReloadable.front.RestartCountHolder;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.component.SampleComponentController;
import com.github.ruediste.rise.sample.db.PageView;
import com.github.ruediste.rise.sample.db.TodoController;

public class WelcomeView extends
        PageView<WelcomeController, WelcomeController.Data> {

    @Inject
    RestartCountHolder holder;

    @Inject
    CoreConfiguration coreConfig;

    @Inject
    SampleBundle sampleBundle;

    @Inject
    PageRenderer renderer;

    @Override
    protected void renderBody(HtmlCanvas html) throws IOException {
        //@formatter:off
		html.nav(class_("navbar navbar-inverse navbar-fixed-top"))
	        .div(class_("container-fluid"))
		        .div(class_("navbar-header"))
		            .button(type("button").class_("navbar-toggle collapsed").data("toggle","collapse").data("target","#navbar").add("aria-expanded","false").add("aria-controls","false"))
		                .span(class_("sr-only"))
		                    .content("Toggle navigation")
		                .span(class_("icon-bar")).content("null")
		                .span(class_("icon-bar")).content("null")
		                .span(class_("icon-bar")).content("null")
		            ._button()
		            .a(class_("navbar-brand").href("#"))
		                .content("Elektronisches Klassenbuch")
		        ._div()
		        .div(id("navbar").class_("navbar-collapse collapse"))
		            .ul(class_("nav navbar-nav navbar-right"))
		                .li()
		                    .a(href("#"))
		                        .content("Dashboard")
		                ._li()
		                .li()
		                    .a(href("#"))
		                        .content("Settings")
		                ._li()
		                .li()
		                    .a(href("#"))
		                        .content("Profile")
		                ._li()
		                .li()
		                    .a(href("#"))
		                        .content("Help")
		                ._li()
		            ._ul()
		            .form(class_("navbar-form navbar-right"))
		                .input(type("text").class_("form-control").add("placeholder","Search..."))
		            ._form()
		        ._div()
		    ._div()
		._nav()
		.div(class_("container-fluid"))
			.div(class_("row"))
				.div(class_("col-sm-3 col-md-2 sidebar"))
					.ul(class_("nav nav-sidebar"))
						.li(class_("active")).a(href("#")).content("Overview")._li()
						.li().a(href("#")).content("Users")._li()
						.li().a(href("#")).content("Teachers")._li()
					._ul()
				._div()
				
				.div(class_("col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main"))
					.h1(class_("page-header")).content("Welcome to RISE")
					.div(class_("row"))
						.div(class_("col-xs-6"))
							.a(class_("btn btn-primary").href(url(go().other()))).content("other")
						._div()
						.div(class_("col-xs-6"))
							.a(class_("btn btn-primary").href(url(go(TodoController.class).index()))).content("Todo Items")
							.a(class_("btn btn-primary").href(url(go().error()))).content("Page with error")
							.a(class_("btn btn-primary").href(
									url(go(SampleComponentController.class).index()))).span(class_("glyphicon glyphicon-search"))._span().content("Component Sample")
						._div()
					._div() 
				._div()
			._div()
		._div();
	}
}
