package com.github.ruediste.laf.sample;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;

public class WelcomeView extends ViewMvcWeb<WelcomeController.Data> {

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().head()._head().body().write("Hello World")._body()._html();
	}

}
