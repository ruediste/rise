package com.github.ruediste.laf.component.web;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.ActionInvocation;
import com.github.ruediste.laf.component.core.RequestHandler;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParseResult;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;

public class ComponentWebInitialRequestParser implements
		RequestParser<HttpRequest> {

	@Inject
	RequestMappingUtil requestMappingUtil;

	private final class ParseResult implements RequestParseResult {
		private ActionInvocation<String> path;

		public ParseResult(ActionInvocation<String> path) {
			this.path = path;
		}

		@Override
		public void handle() {
			initializers.spliterator().forEachRemaining(x -> x.run());
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
	private Iterable<Runnable> initializers;

	public ComponentWebInitialRequestParser initialize(RequestMapper mapper,
			RequestHandler<ActionInvocation<String>> handler,
			Iterable<Runnable> initializers) {
		this.mapper = mapper;
		this.handler = handler;
		this.initializers = initializers;
		return this;
	}

	@Override
	public RequestParseResult parse(HttpRequest request) {
		ActionInvocation<String> path = mapper.parse(request);
		if (path == null) {
			return null;
		}
		return new ParseResult(path);
	}

}
