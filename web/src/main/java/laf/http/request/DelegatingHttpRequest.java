package laf.http.request;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link HttpRequest} implementation delegating to a {@link HttpServletRequest}
 */
public class DelegatingHttpRequest extends HttpRequestBase {

	private HttpServletRequest delegate;

	public DelegatingHttpRequest(HttpServletRequest delegate) {
		this.delegate = delegate;
	}

	@Override
	public String getPath() {
		String pathInfo = delegate.getPathInfo();
		if (pathInfo == null) {
			return "";
		} else {
			return pathInfo.substring(1);
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return delegate.getParameterMap();
	}

	@Override
	public String getParameter(String name) {
		return delegate.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		return delegate.getParameterValues(name);
	}
}
