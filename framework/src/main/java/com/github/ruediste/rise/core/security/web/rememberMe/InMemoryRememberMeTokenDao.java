package com.github.ruediste.rise.core.security.web.rememberMe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.ruediste.rise.core.security.Subject;
import com.github.ruediste.rise.util.Pair;

public class InMemoryRememberMeTokenDao implements RememberMeTokenDao {

    AtomicLong nextId = new AtomicLong();
    private Map<Long, Pair<RememberMeToken, Subject>> tokens = new ConcurrentHashMap<>();

    @Override
    public RememberMeToken loadToken(long id) {
        return tokens.get(id).getA();
    }

    @Override
    public void newToken(RememberMeToken token, Subject subject) {
        token.id = nextId.incrementAndGet();
        tokens.put(token.getId(), Pair.of(token, subject));
    }

    @Override
    public void updateToken(RememberMeToken token) {
        Pair<RememberMeToken, Subject> existing = tokens.get(token.getId());
        tokens.put(token.getId(), Pair.of(token, existing.getB()));
    }

    @Override
    public Subject loadSubject(long id) {
        return tokens.get(id).getB();
    }

}
