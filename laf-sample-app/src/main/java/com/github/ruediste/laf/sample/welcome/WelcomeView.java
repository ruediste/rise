package com.github.ruediste.laf.sample.welcome;

import static org.rendersnake.HtmlAttributesFactory.charset;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.http_equiv;
import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.name;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.front.ReloadCountHolder;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundle;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.core.web.bootstrap.BootstrapBundle;
import com.github.ruediste.laf.sample.db.TodoController;

public class WelcomeView extends
		ViewMvcWeb<WelcomeController, WelcomeController.Data> {

	static class Bundle extends AssetBundle {

		AssetBundleOutput out = new AssetBundleOutput(this);

		@Override
		public void initialize() {
			paths("/assets/welcome.css", "/assets/welcome.js").load().send(out);
		}

	}

	@Inject
	Bundle bundle;

	@Inject
	BootstrapBundle bootstrapBundle;

	@Inject
	ReloadCountHolder holder;

	@Override
	public void render(HtmlCanvas html) throws IOException {
		//@formatter:off
		html.write("<!DOCTYPE html>",false).html()
			.head()
			.meta(charset("UTF-8"))
			.meta(http_equiv("X-UA-Compatible").content("IE=edge"))
			.meta(name("viewport").content("width=device-width, initial-scale=1"))
			.render(cssBundle(bootstrapBundle.out))
			.render(cssBundle(bundle.out))
		._head()
		.body()
		    .nav(class_("navbar navbar-inverse navbar-fixed-top"))
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
						.h1(class_("page-header")).content("Hello Ninas")
						.div(class_("row"))
							.div(class_("col-xs-6"))
								.a(class_("btn btn-primary").href(url(path().other()))).content("other")
							._div()
							.div(class_("col-xs-6"))
								.a(class_("btn btn-primary").href(url(path(TodoController.class).index()))).content("Todo Items")
							._div()
						._div()
					._div()
				._div()
			._div()
			.render(jsLinks(bootstrapBundle.out))
			.render(jsLinks(bundle.out))
			.write("\n")
			.script(type("text/javascript")).content("initReload(\""+url(new PathInfo("/~reloadQuery"))+"\","+holder.get()+");",false)
		._body()._html();
	}
}
