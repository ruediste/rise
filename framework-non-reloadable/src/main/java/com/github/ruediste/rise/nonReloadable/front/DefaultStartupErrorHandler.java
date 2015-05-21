package com.github.ruediste.rise.nonReloadable.front;

import static org.rendersnake.HtmlAttributesFactory.method;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;

import com.github.ruediste.salta.standard.Stage;

public class DefaultStartupErrorHandler implements StartupErrorHandler {

	@Inject
	Logger log;

	@Inject
	Stage stage;

	@Override
	public void handle(Throwable t, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.setContentType("text/html; charset=UTF-8");
			try (PrintWriter writer = response.getWriter()) {
				HtmlCanvas html = new HtmlCanvas(writer);
				// @formatter:off
			html.write("<!DOCTYPE html>",false)
			.html()
				.head()
					.title().content("Startup Error")
				._head()
				.body()
					.h1().content("Startup Error")
					.p()
						.write("The server was unable to start. Contact the system administrator")
					._p();
					if (stage!=Stage.PRODUCTION){
						StringWriter stackTrace = new StringWriter();
						PrintWriter printWriter = new PrintWriter(stackTrace);
						t.printStackTrace(printWriter);
						printWriter.flush();
						html.p().content("Exception:")
						.pre()
							.write(stackTrace.toString())
						._pre();
					}
					if (stage==Stage.DEVELOPMENT){
						html.form(method("POST").action(""))
							.input(type("submit").value("Drop-and-Create Database"))
						._form();
					}
				html._body()
			._html();
			// @formatter:on

			}
		} catch (Throwable e) {
			log.error("Error while handling Error ", t);
			log.error("Error in Error Handler ", e);
		}
	}
}
