package com.github.ruediste.rise.core.security.web.rememberMe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.inject.Inject;

import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.util.Pair;

public class InMemoryRememberMeTokenDao implements RememberMeTokenDao {

    @Inject
    InMemoryRememberMeTokenStore store;

    @Override
    public RememberMeToken loadToken(String id) {
        Pair<RememberMeToken, Principal> pair = deSerialize(store.get(id));
        if (pair == null)
            return null;
        return pair.getA();
    }

    @Override
    public RememberMeToken newToken(RememberMeToken token, Principal principal) {
        RememberMeToken result = token.withId(store.getNextId());
        store.put(token.getId(), serialize(Pair.of(result, principal)));
        return result;
    }

    @Override
    public void updateToken(RememberMeToken token) {
        Pair<RememberMeToken, Principal> existing = deSerialize(store.get(token.getId()));
        store.put(token.getId(), serialize(Pair.of(token, existing.getB())));
    }

    @Override
    public Principal loadPrincipal(String id) {
        return deSerialize(store.get(id)).getB();
    }

    private byte[] serialize(Pair<RememberMeToken, Principal> pair) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(pair);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private Pair<RememberMeToken, Principal> deSerialize(byte[] bytes) {
        if (bytes == null)
            return null;
        Pair<RememberMeToken, Principal> result;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            result = (Pair<RememberMeToken, Principal>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }
}
