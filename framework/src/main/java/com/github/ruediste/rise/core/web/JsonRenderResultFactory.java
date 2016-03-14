package com.github.ruediste.rise.core.web;

import javax.inject.Inject;

import org.json.simple.JSONValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

public class JsonRenderResultFactory {

    @Inject
    ObjectMapper objectMapper;

    public HttpRenderResult jsonRenderResult(Object result) {
        return new ContentRenderResult(JSONValue.toJSONString(result).getBytes(Charsets.UTF_8),
                "application/json; charset=UTF-8");

    }
}
