package laf.core.http;

import javax.inject.Inject;

public class HttpRenderResultUtil {
	@Inject
	public HttpService httpService;

	public HttpService getHttpService() {
		return httpService;
	}

}
