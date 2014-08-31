package laf.core.http;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class HttpService {
	@Inject
	HttpServletRequest request;

	@Inject
	HttpServletResponse response;

	public String url(String path) {
		String prefix = request.getContextPath();
		prefix += request.getServletPath();
		return response.encodeURL(prefix + "/" + path);
	}

	public String redirectUrl(String path) {
		String prefix = request.getContextPath();
		prefix += request.getServletPath();
		return response.encodeRedirectURL(prefix + "/" + path);
	}

	/**
	 * Return the URL of a resource
	 */
	public String resourceUrl(String resource) {
		return response.encodeURL(request.getContextPath() + "/static/"
				+ resource);
	}
}
