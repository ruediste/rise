package com.github.ruediste.rise.test;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.mvc.MvcUtil;

@Singleton
public class IntegrationTestUtil {

    private String baseUrl;

    public void initialize(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Inject
    MvcUtil util;

    @Inject
    CoreUtil coreUtil;

    public String url(ActionResult result) {
        return url(coreUtil.toPathInfo(result));
    }

    public String url(PathInfo pathInfo) {
        return baseUrl + pathInfo.getValue();
    }

    public <T extends IController> T go(Class<T> controllerClass) {
        return coreUtil.go(controllerClass);
    }
}
