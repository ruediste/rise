package com.github.ruediste.rise.core.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.HttpService;

@Singleton
public class HttpRenderResultUtil {
    @Inject
    public HttpService httpService;

    public HttpService getHttpService() {
        return httpService;
    }

}
