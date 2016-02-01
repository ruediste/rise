package com.github.ruediste.rise.sample;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.core.persistence.EMUtil;

@Singleton
public class UserRepository {

    @Inject
    EntityManager em;

    public Optional<User> getUser(String name) {
        List<User> result = EMUtil.queryWithFilter(em, User.class, ctx -> {
            ctx.addWhere(ctx.cb().equal(ctx.root().get(User_.name), name));
        }).getResultList();
        if (result.size() == 1)
            return Optional.of(result.get(0));
        else
            return Optional.empty();

    }

    public Optional<User> getUser(long userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }
}
