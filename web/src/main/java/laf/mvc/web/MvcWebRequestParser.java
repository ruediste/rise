package laf.mvc.web;

import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;
import laf.mvc.RequestHandler;
import laf.mvc.actionPath.ActionPath;

public class MvcWebRequestParser implements RequestParser<HttpRequest> {

	private final class ParseResult implements RequestParseResult<HttpRequest> {
		private ActionPath<String> path;

		public ParseResult(ActionPath<String> path) {
			this.path = path;
		}

		@Override
		public void handle(HttpRequest request) {
			handler.handle(path);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}

			ParseResult other = (ParseResult) obj;
			return path.isCallToSameActionMethod(other.path);
		}

		@Override
		public int hashCode() {
			return path.hashCode();
		}
	}

	private HttpRequestMapper mapper;
	private RequestHandler<String> handler;

	public void initialize(HttpRequestMapper mapper,
			RequestHandler<String> handler) {
		this.mapper = mapper;
		this.handler = handler;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		ActionPath<String> path = mapper.parse(request);
		if (path == null) {
			return null;
		}
		return new ParseResult(path);
	}

}
