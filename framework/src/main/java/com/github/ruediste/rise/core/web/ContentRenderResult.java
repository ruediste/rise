package com.github.ruediste.rise.core.web;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;

public class ContentRenderResult implements HttpRenderResult {
    public final byte[] content;
    final private HttpServletResponseCustomizer responseCustomizer;

    public ContentRenderResult(byte[] content, String contentType) {
        this.content = content;
        this.responseCustomizer = r -> r.setContentType(contentType);
    }

    public ContentRenderResult(String content, String contentType) {
        this.responseCustomizer = r -> {
            r.setContentType(contentType);
        };
        this.content = content.getBytes(Charsets.UTF_8);
    }

    public static ContentRenderResult json(String content) {
        return new ContentRenderResult(content, "application/json; charset=UTF-8");
    }

    public static ContentRenderResult string(String content, int code) {
        return new ContentRenderResult((content == null ? "" : content).getBytes(Charsets.UTF_8), r -> {
            r.setContentType("text/plain; charset=UTF-8");
            r.setStatus(code);
        });
    }

    public ContentRenderResult(byte[] content, HttpServletResponseCustomizer responseCustomizer) {
        this.content = content;
        this.responseCustomizer = responseCustomizer;
    }

    @Override
    public void sendTo(HttpServletResponse response, HttpRenderResultUtil util) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        responseCustomizer.customizeServletResponse(response);
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(content);
            out.flush();
        }
    }
}
