package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.core.ActionResult;

/**
 * Contains the result of handling a http request. The result should be kept in
 * a form which can be sent to the {@link HttpServletResponse} with minimal
 * dependencies, such that sending the result does generally not fail, except if
 * there are problems with the connection to the client.
 */
public interface HttpRenderResult extends ActionResult {

    /**
     * Send the render result to the given response.
     */
    public void sendTo(HttpServletResponse response, HttpRenderResultUtil util) throws IOException;
}
