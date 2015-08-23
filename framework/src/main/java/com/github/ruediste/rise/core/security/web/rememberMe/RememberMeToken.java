package com.github.ruediste.rise.core.security.web.rememberMe;

import java.io.Serializable;

public class RememberMeToken implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    private final byte[] series;
    private final byte[] token;

    public RememberMeToken(long id, byte[] series, byte[] token) {
        super();
        this.id = id;
        this.series = series;
        this.token = token;
    }

    public RememberMeToken withToken(byte[] token) {
        return new RememberMeToken(id, series, token);
    }

    public RememberMeToken withId(long id) {
        return new RememberMeToken(id, series, token);
    }

    public long getId() {
        return id;
    }

    public byte[] getSeries() {
        return series;
    }

    public byte[] getToken() {
        return token;
    }
}