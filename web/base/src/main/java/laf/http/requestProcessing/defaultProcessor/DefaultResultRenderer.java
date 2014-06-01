package laf.http.requestProcessing.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.configuration.ConfigurationValue;
import laf.http.ContentType;
import laf.http.HttpRenderResult;

public class DefaultResultRenderer implements ResultRenderer {

	@Inject
	ConfigurationValue<ContentType> contentType;

	@Override
	public boolean renderResult(ActionResult result,
			HttpServletResponse response) throws IOException {
		if (result instanceof HttpRenderResult) {
			HttpRenderResult renderResult = (HttpRenderResult) result;
			response.setContentType(contentType.value().get());
			renderResult.sendTo(response);
			return true;
		} else {
			return false;
		}
	}
}