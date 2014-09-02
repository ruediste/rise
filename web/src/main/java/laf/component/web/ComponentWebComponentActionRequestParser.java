package laf.component.web;

import javax.inject.Inject;

import laf.component.core.ComponentActionRequest;
import laf.component.core.RequestHandler;
import laf.component.core.pageScope.PageScopeManager;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

public class ComponentWebComponentActionRequestParser implements
		RequestParser<HttpRequest> {

	@Inject
	PageScopeManager pageScopeManager;

	private RequestHandler<ComponentActionRequest> handler;
	private String prefix;

	private RequestMappingUtilInitializer requestMappingUtilInitializer;

	/**
	 *
	 * @param mapper
	 *            only used to initialize the {@link RequestMappingUtil}
	 * @return
	 */
	public ComponentWebComponentActionRequestParser initialize(String prefix,
			RequestHandler<ComponentActionRequest> handler,
			RequestMappingUtilInitializer requestMappingUtilInitializer) {
		this.prefix = prefix;
		this.handler = handler;
		this.requestMappingUtilInitializer = requestMappingUtilInitializer;
		return this;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		String[] parts = request.getPath().split("/");
		if (!prefix.equals(parts[0])) {
			return null;
		}
		if (parts.length != 3) {
			throw new RuntimeException(
					"expected <reload>/<pageNr>/<componentNr>");
		}

		final ComponentActionRequest actionRequest = new ComponentActionRequest();
		actionRequest.request = request;
		actionRequest.pageNr = Integer.parseInt(parts[1]);
		actionRequest.componentNr = Integer.parseInt(parts[2]);

		return new RequestParseResult<HttpRequest>() {

			@Override
			public void handle(HttpRequest request) {
				requestMappingUtilInitializer.performInitialization();
				pageScopeManager.enter(actionRequest.pageNr);
				try {
					handler.handle(actionRequest);
				} finally {
					pageScopeManager.leave();
				}
			}
		};
	}

}
