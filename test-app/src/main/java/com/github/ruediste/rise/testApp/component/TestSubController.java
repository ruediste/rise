package com.github.ruediste.rise.testApp.component;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.core.persistence.OwnEntityManagers;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.persistence.TestAppEntity;

@OwnEntityManagers
public class TestSubController extends SubViewController {

    public static class View extends ViewComponent<TestSubController> {

        @Override
        protected void render(TestCanvas html) {
            html.VALUE(() -> "")

            .span().CLASS("value").direct(() -> html.write(controller.entity.getValue()))._span()
                    // .add(new
                    // CTextField().bindText(()->controller.entity().getValue()));
                    .input().TYPE("text").VALUE(() -> controller.entity.getValue())

            .render(new CButton("refresh").CLASS("refresh").setHandler(controller::refresh))

            .render(new CButton("save").CLASS("save").setHandler(controller::save));

            // html.add(new CFormGroup(new CTextField().bind(g ->
            // g.setText(controller.entity().getValue()))));
        }
    }

    @Inject
    EntityManager em;

    @Inject
    ComponentUtil util;

    @Inject
    Logger log;

    TestAppEntity entity;

    @Inject
    EntityManagerHolder holder;

    public void initialize(long id) {
        entity = em.find(TestAppEntity.class, id);
        if (entity == null)
            throw new RuntimeException("TestEntity " + id + " not found");
    }

    public void refresh() {
        em.refresh(entity);
        pullUp();
    }

    public void save() {
        pushDown();
        util.commit();
    }
}
