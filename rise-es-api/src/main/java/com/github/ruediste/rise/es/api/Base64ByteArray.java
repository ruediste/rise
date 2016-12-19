package com.github.ruediste.rise.es.api;

import java.util.Base64;
import java.util.Objects;

public class Base64ByteArray {
    private String content;

    public Base64ByteArray(String content) {
        this.content = content;
    }

    public Base64ByteArray(byte[] bb) {
        content = Base64.getEncoder().encodeToString(bb);
    }

    public byte[] get() {
        return Base64.getDecoder().decode(content);
    }

    public String getString() {
        return content;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Base64ByteArray other = (Base64ByteArray) obj;
        return Objects.equals(content, other.content);
    }

}
