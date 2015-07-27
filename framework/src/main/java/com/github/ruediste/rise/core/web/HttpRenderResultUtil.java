package com.github.ruediste.rise.core.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.HttpService;

@Singleton
public class HttpRenderResultUtil {
    @Inject
    HttpService httpService;

    @Inject
    CoreRequestInfo coreRequestInfo;

    public HttpService getHttpService() {
        return httpService;
    }

    public CoreRequestInfo getCoreRequestInfo() {
        return coreRequestInfo;
    }

}
