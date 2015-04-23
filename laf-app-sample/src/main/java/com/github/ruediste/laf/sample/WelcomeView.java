package com.github.ruediste.laf.sample;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;

public class WelcomeView extends ViewMvcWeb<WelcomeController.Data> {

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().head()._head().body().write("Hello World")
				.a(href(url(path(WelcomeController.class).other())))
				.content("other")._body()._html();
	}

}
