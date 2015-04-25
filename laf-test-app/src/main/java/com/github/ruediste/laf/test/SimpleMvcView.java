package com.github.ruediste.laf.test;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;

public class SimpleMvcView extends ViewMvcWeb<String> {

	@Override
	public void render(HtmlCanvas html) throws IOException {
		html.html().body().write(data())._body()._html();
	}

}
