package com.github.ruediste.rise.core.security.web.rememberMe;

public class RememberMeToken {
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
        return new RememberMeToken(getId(), getSeries(), token);
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