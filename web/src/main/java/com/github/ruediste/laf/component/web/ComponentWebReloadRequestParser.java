package com.github.ruediste.laf.component.web;

import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.PageReloadRequest;
import com.github.ruediste.laf.component.core.RequestHandler;
import com.github.ruediste.laf.component.core.pageScope.PageScopeManager;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParseResult;
import com.github.ruediste.laf.core.requestParserChain.RequestParser;

public class ComponentWebReloadRequestParser implements
		RequestParser<HttpRequest> {

	@Inject
	PageScopeManager manager;

	@Inject
	RequestMappingUtil requestMappingUtil;

	private RequestHandler<PageReloadRequest> handler;
	private String prefix;

	private List<Runnable> utilInitializers;

	public ComponentWebReloadRequestParser initialize(String prefix,
			RequestHandler<PageReloadRequest> handler,
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
		String path = request.getPathInfo().substring(prefix.length());
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		String[] parts = path.split("/");
		if (parts.length != 2) {
			throw new RuntimeException(
					"expected <reload>/<pageNr>/<componentNr>");
		}

		final PageReloadRequest reloadRequest = new PageReloadRequest();
		reloadRequest.pageNr = Integer.parseInt(parts[0]);
		reloadRequest.componentNr = Integer.parseInt(parts[1]);

		return new RequestParseResult() {

			@Override
			public void handle() {
				manager.enter(reloadRequest.pageNr);
				try {
					utilInitializers.stream().forEach(x -> x.run());
					handler.handle(reloadRequest);
				} finally {
					manager.leave();
				}

			}
		};
	}

}
