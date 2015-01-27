package com.github.ruediste.laf.component.web;

import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.ComponentActionRequest;
import com.github.ruediste.laf.component.core.RequestHandler;
import com.github.ruediste.laf.component.core.pageScope.PageScopeManager;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParseResult;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;

public class ComponentWebComponentActionRequestParser implements
		RequestParser<HttpRequest> {

	@Inject
	PageScopeManager pageScopeManager;

	private RequestHandler<ComponentActionRequest> handler;
	private String prefix;

	private List<Runnable> utilInitializers;

	/**
	 *
	 * @param mapper
	 *            only used to initialize the {@link RequestMappingUtil}
	 * @return
	 */
	public ComponentWebComponentActionRequestParser initialize(String prefix,
			RequestHandler<ComponentActionRequest> handler,
			List<Runnable> utilInitializers) {
		this.prefix = prefix;
		this.handler = handler;
		this.utilInitializers = utilInitializers;
		return this;
	}

	@Override
	public RequestParseResult parse(HttpRequest request) {
		if (!request.getPathInfo().startsWith(prefix)) {
			return null;
		}
		String[] parts = request.getPathInfo().substring(prefix.length())
				.split("/");

		if (parts.length != 2) {
			throw new RuntimeException(
					"expected <reload>/<pageNr>/<componentNr>");
		}

		final ComponentActionRequest actionRequest = new ComponentActionRequest();
		actionRequest.request = request;
		actionRequest.pageNr = Integer.parseInt(parts[1]);
		actionRequest.componentNr = Integer.parseInt(parts[2]);

		return new RequestParseResult() {

			@Override
			public void handle() {
				utilInitializers.stream().forEach(x -> x.run());
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
