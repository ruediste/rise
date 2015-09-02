package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class RedirectToRefererRenderResult implements HttpRenderResult {

    @Override
    public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
            throws IOException {
        response.sendRedirect(util.getCoreUtil().refererUrl());
    }
}
