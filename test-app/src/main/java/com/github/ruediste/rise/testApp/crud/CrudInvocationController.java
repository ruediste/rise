package com.github.ruediste.rise.testApp.crud;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.mvc.Updating;
import com.github.ruediste.rise.testApp.persistence.TestEntity;

public class CrudInvocationController extends
        ControllerMvc<CrudInvocationController> {

    @Inject
    EntityManager em;

    @Updating
    public ActionResult browseTestEnties() {
        TestEntity e = new TestEntity();
        e.setValue(new Date().toString());
        em.persist(e);
        return redirect(go(TestCrudController.class).browse(TestEntity.class));
    }
}
