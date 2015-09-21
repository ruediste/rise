package com.github.ruediste.rise.core.web;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.ruediste.rise.util.Pair;

/**
 * Specification for generating an URL
 */
public class UrlSpec implements Serializable {
    private static final long serialVersionUID = 1L;
    private final PathInfo pathInfo;
    private final List<Pair<String, String>> parameters;

    public UrlSpec(PathInfo pathInfo) {
        this.pathInfo = pathInfo;
        parameters = Collections.emptyList();
    }

    public UrlSpec(PathInfo pathInfo, List<Pair<String, String>> parameters) {
        this.pathInfo = pathInfo;
        this.parameters = new ArrayList<Pair<String, String>>(parameters);
    }

    public UrlSpec(PathInfo pathInfo, Map<String, String[]> parameterMap) {
        this.pathInfo = pathInfo;
        this.parameters = parameterMap.entrySet()
                .stream().<Pair<String, String>> flatMap(e -> Arrays
                        .stream(e.getValue()).map(v -> Pair.of(e.getKey(), v)))
                .collect(toList());
    }

    public PathInfo getPathInfo() {
        return pathInfo;
    }

    public List<Pair<String, String>> getParameters() {
        return parameters;
    }

    public String urlSuffix() {
        String url = getPathInfo().getValue();
        if (!getParameters().isEmpty()) {
            url += "?";
            url += getParameters().stream().map(p -> p.getA() + "=" + p.getB())
                    .collect(joining("&"));
        }
        return url;
    }
}
