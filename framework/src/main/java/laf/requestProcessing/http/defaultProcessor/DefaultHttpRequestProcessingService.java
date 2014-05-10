package laf.requestProcessing.http.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.base.RenderResult;
import laf.httpRequest.DelegatingHttpRequest;
import laf.httpRequestMapping.HttpRequestMappingService;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.initialization.LafInitializer;
import laf.initialization.laf.LafInitializationPhase;
import laf.requestProcessing.http.HttpRequestProcessingService;

@Singleton
public class DefaultHttpRequestProcessingService extends
		HttpRequestProcessingService {
	@Inject
	HttpRequestMappingService httpRequestMappingService;

	public class RequestParserImpl implements RequestParser {

		@Override
		public ActionPath<ParameterValueProvider> parse(
				HttpServletRequest request) {
			ActionPath<ParameterValueProvider> actionPath = httpRequestMappingService
					.parse(new DelegatingHttpRequest(request));

			if (actionPath == null) {
				throw new RuntimeException("URL could not be handled "
						+ request.getPathInfo());
			}
			return actionPath;
		}

		@LafInitializer(phase = LafInitializationPhase.class, after = HttpRequestMappingService.class)
		public void initialize() {

		}

	}

	public class ResultRendererImpl implements ResultRenderer {
		@Override
		public void renderResult(ActionResult result,
				HttpServletResponse response) throws IOException {
			RenderResult renderResult = (RenderResult) result;
			renderResult.sendTo(response);
		}
	}
}
