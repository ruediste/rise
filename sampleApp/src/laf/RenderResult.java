package laf;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Contains the result of handling a http request. The result should be kept in
 * a form which can be sent to the {@link HttpServletResponse} with minimal
 * dependencies and the sending process should generally not fail, except if
 * there are problems with the response itself.
 */
public interface RenderResult {

	/**
	 * Send the render result to the given response.
	 * 
	 * @throws IOException
	 *             if there is an issue with the provided response
	 */
	public void sendTo(HttpServletResponse response) throws IOException;
}
