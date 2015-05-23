package com.github.ruediste.rise.nonReloadable.front;

import static org.rendersnake.HtmlAttributesFactory.method;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.salta.standard.Stage;

public class DefaultStartupErrorHandler implements StartupErrorHandler {

	private static final Logger log = LoggerFactory
			.getLogger(DefaultStartupErrorHandler.class);

	@Inject
	private DataBaseLinkRegistry registry;

	protected Stage stage;

	private String dropAndCreatePathInfo;

	public DefaultStartupErrorHandler() {
		this("/~dropAndCreateDb");
	}

	public DefaultStartupErrorHandler(String dropAndCreatePathInfo) {
		this.dropAndCreatePathInfo = dropAndCreatePathInfo;
	}

	@Override
	public void handle(Throwable t, HttpServletRequest request,
			HttpServletResponse response) {
		if (stage == Stage.DEVELOPMENT
				&& dropAndCreatePathInfo.equals(request.getPathInfo())) {
			dropAndCreateDb(request, response);
		}
		try {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
					if (stage!=null && stage!=Stage.PRODUCTION){
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
						html.form(method("POST").action(dropAndCreatePathInfo))
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

	protected void dropAndCreateDb(HttpServletRequest request,
			HttpServletResponse response) {
		if (registry == null) {
			log.error("Cannot drop-and-create the database since the NonRestartable Application could not be started");
		}
		log.info("Dropping and Creating DB schemas ...");
		registry.dropAndCreateSchemas();
		redirectToReferer(request, response);
	}

	protected void redirectToReferer(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.sendRedirect(request.getHeader("Referer"));
		} catch (IOException e) {
			log.error("Error while sending redirect", e);
		}
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
