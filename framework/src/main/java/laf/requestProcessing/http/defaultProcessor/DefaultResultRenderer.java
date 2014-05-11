package laf.requestProcessing.http.defaultProcessor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.RenderResult;
import laf.requestProcessing.http.ResultRenderer;

public class DefaultResultRenderer implements ResultRenderer {
	@Override
	public void renderResult(ActionResult result,
			HttpServletResponse response) throws IOException {
		RenderResult renderResult = (RenderResult) result;
		renderResult.sendTo(response);
	}
}