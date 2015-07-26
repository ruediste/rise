package com.github.ruediste.rise.nonReloadable.front;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasBase;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.google.common.base.Throwables;

public class DefaultStartupErrorHandler implements StartupErrorHandler {

    private static final Logger log = LoggerFactory
            .getLogger(DefaultStartupErrorHandler.class);

    @Inject
    private DataBaseLinkRegistry registry;

    protected ApplicationStage stage;

    private String dropAndCreatePathInfo;

    public DefaultStartupErrorHandler() {
        this("/~dropAndCreateDb");
    }

    public DefaultStartupErrorHandler(String dropAndCreatePathInfo) {
        this.dropAndCreatePathInfo = dropAndCreatePathInfo;
    }

    private static class SimpleCanvas extends HtmlCanvasBase<SimpleCanvas>
            implements Html5Canvas<SimpleCanvas> {

        public SimpleCanvas(PrintWriter writer) {
            initialize(writer);
        }

        @Override
        public SimpleCanvas self() {
            return this;
        }

    }

    @Override
    public void handle(Throwable t, HttpServletRequest request,
            HttpServletResponse response) {
        if (stage == ApplicationStage.DEVELOPMENT
                && dropAndCreatePathInfo.equals(request.getPathInfo())) {
            dropAndCreateDb(request, response);
        }
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/html; charset=UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                SimpleCanvas html = new SimpleCanvas(writer);
                // @formatter:off
			html.writeUnescaped("<!DOCTYPE html>")
			.html()
				.head()
					.title().content("Startup Error")
				._head()
				.body()
					.h1().content("Startup Error")
					.p()
						.write("The server was unable to start. Contact the system administrator")
					._p();
					if (stage!=null && stage!=ApplicationStage.PRODUCTION){
						html.p().content("Exception:")
						.pre()
							.write(Throwables.getStackTraceAsString(t))
						._pre();
					}
					if (stage==ApplicationStage.DEVELOPMENT){
						html.form().METHOD("POST").ACTION(dropAndCreatePathInfo)
							.input().TYPE("submit").VALUE("Drop-and-Create Database")
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
    public void setStage(ApplicationStage stage) {
        this.stage = stage;
    }

}
