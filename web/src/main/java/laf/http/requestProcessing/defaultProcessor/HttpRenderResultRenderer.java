package laf.http.requestProcessing.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.configuration.ConfigurationValue;
import laf.http.ContentType;
import laf.http.HttpRenderResult;
import laf.http.HttpRenderResultUtil;

public class HttpRenderResultRenderer implements ResultRenderer {

	@Inject
	ConfigurationValue<ContentType> contentType;

	@Inject
	HttpRenderResultUtil httpRenderResultUtil;

	@Override
	public boolean renderResult(ActionResult result,
			HttpServletResponse response) throws IOException {
		if (result instanceof HttpRenderResult) {
			HttpRenderResult renderResult = (HttpRenderResult) result;
			response.setContentType(contentType.value().get());
			renderResult.sendTo(response, httpRenderResultUtil);
			return true;
		} else {
			return false;
		}
	}
}