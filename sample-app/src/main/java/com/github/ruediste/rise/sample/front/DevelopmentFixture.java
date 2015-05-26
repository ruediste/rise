package com.github.ruediste.rise.sample.front;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.sample.db.TodoItem;

public class DevelopmentFixture implements Runnable {

    @Inject
    EntityManager em;

    @Override
    public void run() {
        TodoItem item = new TodoItem();
        item.setName("Buy dishwasher tabs");
        em.persist(item);
    }

}
