package laf.component.web.requestProcessing;

import javax.inject.Inject;

import laf.component.core.ActionInvocation;
import laf.component.core.RequestHandler;
import laf.component.web.RequestMappingUtil;
import laf.component.web.RequestMappingUtilInitializer;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

public class ComponentWebInitialRequestParser implements
		RequestParser<HttpRequest> {

	@Inject
	RequestMappingUtil requestMappingUtil;

	private final class ParseResult implements RequestParseResult<HttpRequest> {
		private ActionInvocation<String> path;

		public ParseResult(ActionInvocation<String> path) {
			this.path = path;
		}

		@Override
		public void handle(HttpRequest request) {
			requestMappingUtilInitializer.performInitialization();

			handler.handle(path);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}

			ParseResult other = (ParseResult) obj;
			return path.getInvocation().getMethod()
					.equals(other.path.getInvocation());
		}

		@Override
		public int hashCode() {
			return path.hashCode();
		}
	}

	private RequestMapper mapper;
	private RequestHandler<ActionInvocation<String>> handler;
	private RequestMappingUtilInitializer requestMappingUtilInitializer;

	public ComponentWebInitialRequestParser initialize(RequestMapper mapper,
			RequestHandler<ActionInvocation<String>> handler,
			RequestMappingUtilInitializer requestMappingUtilInitializer) {
		this.mapper = mapper;
		this.handler = handler;
		this.requestMappingUtilInitializer = requestMappingUtilInitializer;
		return this;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		ActionInvocation<String> path = mapper.parse(request);
		if (path == null) {
			return null;
		}
		return new ParseResult(path);
	}

}
