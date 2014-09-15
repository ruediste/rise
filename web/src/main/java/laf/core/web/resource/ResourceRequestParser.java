package laf.core.web.resource;

import java.util.function.Consumer;

import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

public class ResourceRequestParser implements RequestParser<HttpRequest> {

	private Consumer<HttpRequest> handler;
	private String prefix;

	public void initialize(String prefix, Consumer<HttpRequest> handler) {
		this.prefix = prefix;
		this.handler = handler;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		if (request.getPath().startsWith(prefix)) {
			return new RequestParseResult<HttpRequest>() {

				@Override
				public void handle(HttpRequest request) {
					handler.accept(request);
				}
			};
		}
		return null;
	}
}
