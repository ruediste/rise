package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParseResult;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;
import com.github.ruediste.laf.mvc.core.ActionPath;
import com.github.ruediste.laf.mvc.core.RequestHandler;

public class MvcWebRequestParser implements RequestParser {

	@Inject
	RenderUtilImpl renderUtil;

	private final class ParseResult implements RequestParseResult {
		private ActionPath<String> path;

		public ParseResult(ActionPath<String> path) {
			this.path = path;
		}

		@Override
		public void handle() {
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
	public RequestParseResult parse(HttpRequest request) {
		ActionPath<String> path = mapper.parse(request);
		if (path == null) {
			return null;
		}
		return new ParseResult(path);
	}

}
