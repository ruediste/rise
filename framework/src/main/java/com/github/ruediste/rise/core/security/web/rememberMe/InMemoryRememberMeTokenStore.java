package com.github.ruediste.rise.core.security.web.rememberMe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.nonReloadable.front.reload.NonReloadable;

@NonReloadable
@NonRestartable
@Singleton
public class InMemoryRememberMeTokenStore {
    private Map<String, byte[]> tokens = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    public void put(String id, byte[] bytes) {
        tokens.put(id, bytes);
    }

    @SuppressWarnings("unchecked")
    public byte[] get(String id) {
        return tokens.get(id);
    }

    public String getNextId() {
        return String.valueOf(nextId.incrementAndGet());
    }

    public void remove(String id) {
        tokens.remove(id);
    }
}
