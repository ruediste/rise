package com.github.ruediste.rise.core.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.CoreUtil;

@Singleton
public class HttpRenderResultUtil {

    @Inject
    CoreUtil coreUtil;

    @Inject
    CoreRequestInfo coreRequestInfo;

    public CoreRequestInfo getCoreRequestInfo() {
        return coreRequestInfo;
    }

    public CoreUtil getCoreUtil() {
        return coreUtil;
    }

}
