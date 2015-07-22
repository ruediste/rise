package com.github.ruediste.rise.testApp.component;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.OwnEntityManagers;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.testApp.persistence.TestAppEntity;

@OwnEntityManagers
public class TestSubController {

    public static class View extends ViewComponent<TestSubController> {

        @Override
        protected Component createComponents() {
            return toComponent(html -> html

                    .span()
                    .CLASS("value")
                    .add(toComponentDirect(x -> x.write(controller.entity()
                            .getValue())))
                    ._span()

                    .add(new CButton("refresh").CLASS("refresh").handler(
                            controller::refresh))

                    .add(new CButton("save").CLASS("save").handler(
                            controller::save))

                    .add(new CTextField().bind(g -> g
                            .setText(controller.entity().getValue()))));
        }
    }

    @Inject
    EntityManager em;
    @Inject
    ComponentUtil util;

    @Inject
    Logger log;

    @Inject
    private BindingGroup<TestAppEntity> entity;

    public TestAppEntity entity() {
        return entity.proxy();
    }

    @Inject
    EntityManagerHolder holder;

    public void initialize(long id) {
        TestAppEntity e = em.find(TestAppEntity.class, id);
        if (e == null)
            throw new RuntimeException("TestEntity " + id + " not found");
        entity.set(e);
    }

    public void refresh() {
        em.refresh(entity.get());
        entity.pullUp();
    }

    public void save() {
        entity.pushDown();
        util.commit();
    }
}
