package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class RedirectRenderResult implements HttpRenderResult {

    private UrlSpec path;

    public RedirectRenderResult(UrlSpec path) {
        this.path = path;
    }

    @Override
    public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
            throws IOException {
        if ("true".equals(util.getCoreRequestInfo().getServletRequest()
                .getHeader("rise-is-ajax"))) {
            response.setHeader("rise-redirect-target",
                    util.getCoreUtil().url(path));
        } else
            response.sendRedirect(util.getCoreUtil().redirectUrl(path));
    }
}
