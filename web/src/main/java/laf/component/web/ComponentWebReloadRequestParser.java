package laf.component.web;

import java.util.List;

import javax.inject.Inject;

import laf.component.core.PageReloadRequest;
import laf.component.core.RequestHandler;
import laf.component.core.pageScope.PageScopeManager;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

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
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		String[] parts = request.getPath().split("/");
		if (!prefix.equals(parts[0])) {
			return null;
		}
		if (parts.length != 3) {
			throw new RuntimeException(
					"expected <reload>/<pageNr>/<componentNr>");
		}

		final PageReloadRequest reloadRequest = new PageReloadRequest();
		reloadRequest.pageNr = Integer.parseInt(parts[1]);
		reloadRequest.componentNr = Integer.parseInt(parts[2]);

		return new RequestParseResult<HttpRequest>() {

			@Override
			public void handle(HttpRequest request) {
				utilInitializers.stream().forEach(x -> x.run());
				manager.enter(reloadRequest.pageNr);
				try {
					handler.handle(reloadRequest);
				} finally {
					manager.leave();
				}

			}
		};
	}

}
