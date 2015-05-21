package com.github.ruediste.rise.sample.db;

import static org.rendersnake.HtmlAttributesFactory.action;
import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;

import com.github.ruediste.rise.sample.db.TodoController.IndexData;

public class TodoView extends PageView<TodoController, IndexData> {

	@Inject
	Logger log;

	@Override
	protected void renderBody(HtmlCanvas html) throws IOException {

		// @formatter:off
		html
			.div(class_("container"))
				.div(class_("row"))
					.div(class_("col-xs-12"))
						.h1().content("Todo Items")
					._div()
				._div();
		for (TodoItem item : data().allItems) {
			html.div(class_("row"))
				.div(class_("col-xs-9"))
					.write(item.getName())
				._div()
				.div(class_("col-xs-3"))
					.a(class_("btn btn-default").href(url(go().delete(item))))
						.content("delete")
				._div()
			._div();
		}
		html.form(action(url(go().add())).method("POST"))
		.div(class_("row"))
			.div(class_("col-xs-9"))
				.input(class_("form-control").type("text").name("name"))
			._div()
			.div(class_("col-xs-3"))
					.input(class_("form-control").type("submit").value("add"))
				._div()
		._div()
		._form()
		._div();
	}
}
