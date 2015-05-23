package com.github.ruediste.rise.nonReloadable.front;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.nonReloadable.ApplicationStage;

/**
 * Handler for errors occurring during startup of the application. Instances may
 * not depend on dependency injection.
 */
public interface StartupErrorHandler {

    void setStage(ApplicationStage stage);

    /**
     * Render the error response. To trigger the error handling of the
     * container, just throw an exception.
     */
    void handle(Throwable startupError, HttpServletRequest request,
            HttpServletResponse response);

}
