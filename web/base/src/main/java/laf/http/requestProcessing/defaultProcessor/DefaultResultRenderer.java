package laf.http.requestProcessing.defaultProcessor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.http.HttpRenderResult;

public class DefaultResultRenderer implements ResultRenderer {
	@Override
	public boolean renderResult(ActionResult result,
			HttpServletResponse response) throws IOException {
		if (result instanceof HttpRenderResult) {
			HttpRenderResult renderResult = (HttpRenderResult) result;
			renderResult.sendTo(response);
			return true;
		} else {
			return false;
		}
	}
}