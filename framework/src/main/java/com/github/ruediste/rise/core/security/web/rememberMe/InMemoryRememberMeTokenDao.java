package com.github.ruediste.rise.core.security.web.rememberMe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.inject.Inject;

import com.github.ruediste.rise.core.security.Subject;
import com.github.ruediste.rise.util.Pair;

public class InMemoryRememberMeTokenDao implements RememberMeTokenDao {

    @Inject
    InMemoryRememberMeTokenStore store;

    @Override
    public RememberMeToken loadToken(long id) {
        return deSerialize(store.get(id)).getA();
    }

    @Override
    public RememberMeToken newToken(RememberMeToken token, Subject subject) {
        RememberMeToken result = token.withId(store.getNextId());
        store.put(token.getId(), serialize(Pair.of(result, subject)));
        return result;
    }

    @Override
    public void updateToken(RememberMeToken token) {
        Pair<RememberMeToken, Subject> existing = deSerialize(store.get(token
                .getId()));
        store.put(token.getId(), serialize(Pair.of(token, existing.getB())));
    }

    @Override
    public Subject loadSubject(long id) {
        return deSerialize(store.get(id)).getB();
    }

    private byte[] serialize(Pair<RememberMeToken, Subject> pair) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(pair);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private Pair<RememberMeToken, Subject> deSerialize(byte[] bytes) {
        if (bytes == null)
            return null;
        Pair<RememberMeToken, Subject> result;
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(bytes))) {
            result = (Pair<RememberMeToken, Subject>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void delete(long id) {
        store.remove(id);
    }
}
