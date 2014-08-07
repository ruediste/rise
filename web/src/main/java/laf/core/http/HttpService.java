package laf.core.http;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.core.actionPath.ActionPath;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.HttpRequestMappingService;

@ApplicationScoped
public class HttpService {
	@Inject
	HttpServletRequest request;

	@Inject
	HttpServletResponse response;

	@Inject
	HttpRequestMappingService httpRequestMappingService;

	public String url(ActionResult path) {
		@SuppressWarnings("unchecked")
		HttpRequest url = httpRequestMappingService
		.generate((ActionPath<Object>) path);
		return url(url.getPathWithParameters());
	}

	public String url(String path) {
		String prefix = request.getContextPath();
		prefix += request.getServletPath();
		return response.encodeURL(prefix + "/" + path);
	}

	public String redirectUrl(ActionResult path) {
		@SuppressWarnings("unchecked")
		HttpRequest url = httpRequestMappingService
		.generate((ActionPath<Object>) path);
		return redirectUrl(url.getPathWithParameters());
	}

	public String redirectUrl(String path) {
		String prefix = request.getContextPath();
		prefix += request.getServletPath();
		return response.encodeRedirectURL(prefix + "/" + path);
	}

}
