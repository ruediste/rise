package com.github.ruediste.rise.core.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ContentRenderResult implements HttpRenderResult {
    public final byte[] content;
    final private HttpServletResponseCustomizer responseCustomizer;

    public ContentRenderResult(byte[] content, String contentType) {
        this.content = content;
        this.responseCustomizer = r -> r.setContentType(contentType);
    }

    public ContentRenderResult(String content, String contentType) {
        this.responseCustomizer = r -> r.setContentType(contentType);
        try {
            this.content = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ContentRenderResult(byte[] content,
            HttpServletResponseCustomizer responseCustomizer) {
        this.content = content;
        this.responseCustomizer = responseCustomizer;
    }

    @Override
    public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        responseCustomizer.customizeServletResponse(response);
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(content);
            out.flush();
        }
    }
}