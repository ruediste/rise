package com.github.ruediste.rise.core.web;

import javax.inject.Inject;

import com.github.ruediste.rise.core.HttpService;

public class HttpRenderResultUtil {
    @Inject
    public HttpService httpService;

    public HttpService getHttpService() {
        return httpService;
    }

}
