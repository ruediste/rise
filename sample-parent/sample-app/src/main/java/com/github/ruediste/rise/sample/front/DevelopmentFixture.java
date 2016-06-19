package com.github.ruediste.rise.sample.front;

import java.util.Arrays;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.core.security.login.PasswordHashingService;
import com.github.ruediste.rise.sample.User;
import com.github.ruediste.rise.sample.db.TodoCategory;
import com.github.ruediste.rise.sample.db.TodoItem;

public class DevelopmentFixture implements Runnable {

    @Inject
    EntityManager em;

    @Inject
    PasswordHashingService hashingService;

    @Override
    public void run() {
        TodoItem item = new TodoItem();
        item.setName("Buy dishwasher tabs");
        em.persist(item);

        TodoCategory category = new TodoCategory();
        category.setName("Private");
        em.persist(category);

        User user = new User();
        user.setName("admin");
        user.setHash(hashingService.createHash("admin"));
        user.getGrantedRights().addAll(Arrays.asList(SampleRight.VIEW_ADMIN_PAGE, SampleRight.VIEW_USER_PAGE));
        em.persist(user);

        user = new User();
        user.setName("user");
        user.setHash(hashingService.createHash("user"));
        user.getGrantedRights().addAll(Arrays.asList(SampleRight.VIEW_USER_PAGE));
        em.persist(user);

    }

}
