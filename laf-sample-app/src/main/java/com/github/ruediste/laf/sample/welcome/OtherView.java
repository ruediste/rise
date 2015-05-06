package com.github.ruediste.laf.sample.welcome;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.mvc.web.MvcUtil;

public class OtherView extends ViewMvcWeb<WelcomeController, String> {

	@Inject
	MvcUtil util;

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().head()._head().body().h1().content("TheOther")
				.a(href(url(go().index()))).content("index")._body()._html();
	}

}
