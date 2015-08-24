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
    private Map<Long, byte[]> tokens = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    public void put(long id, byte[] bytes) {
        tokens.put(id, bytes);
    }

    @SuppressWarnings("unchecked")
    public byte[] get(long id) {
        return tokens.get(id);
    }

    public long getNextId() {
        return nextId.incrementAndGet();
    }

    public void remove(long id) {
        tokens.remove(id);
    }
}
