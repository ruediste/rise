package laf.httpRequestProcessing.defaultProcessor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;

public interface ResultRenderer {
	void renderResult(ActionResult result, HttpServletResponse response)
			throws IOException;
}