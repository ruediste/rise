package com.github.ruediste.laf.core;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;

import com.github.ruediste.laf.core.web.PathInfo;

/**
 * Service providing convenience methods to work with HTTP requests and
 * responses
 */
@Singleton
public class HttpService {
	@Inject
	CoreRequestInfo coreRequestInfo;

	String contextPath;

	@PostConstruct
	public void postConstruct(@Permanent ServletConfig servletConfig) {
		contextPath = servletConfig.getServletContext().getContextPath();
	}

	public String urlStatic(PathInfo path) {
		String prefix = contextPath;
		return prefix + path.getValue();
	}

	public String url(PathInfo path) {
		String prefix = contextPath;
		prefix += coreRequestInfo.getServletRequest().getServletPath();
		return coreRequestInfo.getServletResponse().encodeURL(
				prefix + path.getValue());
	}

	public String redirectUrl(PathInfo path) {
		String prefix = coreRequestInfo.getServletRequest().getContextPath();
		prefix += coreRequestInfo.getServletRequest().getServletPath();
		return coreRequestInfo.getServletResponse().encodeRedirectURL(
				prefix + path.getValue());
	}

}
