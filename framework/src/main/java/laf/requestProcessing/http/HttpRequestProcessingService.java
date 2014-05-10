package laf.requestProcessing.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public class HttpRequestProcessingService {

	public interface RequestParser {
		ActionPath<ParameterValueProvider> parse(HttpServletRequest request);
	}

	public interface ResultRenderer {
		void renderResult(ActionResult result, HttpServletResponse response)
				throws IOException;
	}

	public interface HttpRequestProcessor {
		void process(HttpServletRequest request, HttpServletResponse response);
	}

}