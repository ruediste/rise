package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class RedirectRenderResult implements HttpRenderResult {

    private PathInfo path;

    public RedirectRenderResult(PathInfo path) {
        this.path = path;
    }

    @Override
    public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
            throws IOException {
        response.sendRedirect(util.httpService.redirectUrl(path));
    }
}
