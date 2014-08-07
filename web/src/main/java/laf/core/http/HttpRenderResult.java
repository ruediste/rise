package laf.core.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;

/**
 * Contains the result of handling a http request. The result should be kept in
 * a form which can be sent to the {@link HttpServletResponse} with minimal
 * dependencies and the sending process should generally not fail, except if
 * there are problems with the response itself.
 */
public interface HttpRenderResult extends ActionResult {

	/**
	 * Send the render result to the given response.
	 * @param util TODO
	 * 
	 * @throws IOException
	 *             if there is an issue with the provided response
	 */
	public void sendTo(HttpServletResponse response, HttpRenderResultUtil util) throws IOException;
}
