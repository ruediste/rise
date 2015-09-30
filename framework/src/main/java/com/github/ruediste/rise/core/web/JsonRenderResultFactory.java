package com.github.ruediste.rise.core.web;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRenderResultFactory {

    @Inject
    ObjectMapper objectMapper;

    public HttpRenderResult jsonRenderResult(Object result) {
        try {
            return new ContentRenderResult(
                    objectMapper.writeValueAsBytes(result), "text/json");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error while rendering json result", e);
        }

    }
}
