package laf.core.http.requestProcessing.defaultProcessor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;

public interface ResultRenderer {
	/**
	 * Render the given action result the the http resonse
	 *
	 * @return true if the result has been rendered, false otherwise
	 */
	boolean renderResult(ActionResult result, HttpServletResponse response)
			throws IOException;
}