package com.github.ruediste.rise.testApp.component;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.testApp.persistence.TestAppEntity;

public class TestComponentHelperController extends
        ControllerMvc<TestComponentHelperController> {

    @Inject
    EntityManager em;

    @Updating
    public ActionResult index() {

        TestAppEntity e = new TestAppEntity();
        e.setValue("Hello World");
        em.persist(e);
        return redirect(go(TestComponentController.class).initialize(e.getId()));
    }
}
