package com.github.ruediste.laf.sample.db;

import static org.rendersnake.HtmlAttributesFactory.action;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.sample.db.TodoController.IndexData;

public class IndexView extends PageView<TodoController, IndexData> {

	@Override
	protected void renderBody(HtmlCanvas html) throws IOException {
		//@formatter:off
		html.h1().content("Todo Items").ul();
		for (TodoItem item : data().allItems) {
			html.li()
				.write(item.getName())
				.a(href(url(path().delete()))).content("delete")
			._li();
		}
		html._ul()
		.form(action(url(path().add())))
			.input(type("text").name("name"))
			.input(type("submit").value("add"))
		._form();
	}
}
