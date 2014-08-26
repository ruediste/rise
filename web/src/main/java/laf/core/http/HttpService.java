package laf.core.http;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;

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

	public void startHtmlPage(HtmlCanvas html) throws IOException {
		html.write(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">",
				false);
		html.html(HtmlAttributesFactory.xmlns("http://www.w3.org/1999/xhtml"));
	}

	/**
	 * Return the URL of a resource
	 */
	public String resourceUrl(String resource) {
		return response.encodeURL(request.getContextPath() + "/static/"
				+ resource);
	}
}
