package com.github.ruediste.rise.test;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.mvc.MvcUtil;

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

    public <T extends IControllerMvc> T go(Class<T> controllerClass) {
        return util.path(controllerClass).go();
    }
}
