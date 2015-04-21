package com.github.ruediste.laf.core;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.laf.core.web.PathInfo;

/**
 * Service providing convenience methods to work with HTTP requests and
 * responses
 */
@Singleton
public class HttpService {
	@Inject
	CoreRequestInfo coreRequestInfo;

	public String url(PathInfo path) {
		String prefix = coreRequestInfo.getServletRequest().getContextPath();
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
