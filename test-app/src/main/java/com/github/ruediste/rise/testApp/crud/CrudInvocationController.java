package com.github.ruediste.rise.testApp.crud;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.persistence.Updating;

public class CrudInvocationController extends
        ControllerMvc<CrudInvocationController> {

    @Inject
    EntityManager em;

    @Updating
    public ActionResult browseTestEnties() {
        TestCrudEntityA e = new TestCrudEntityA();
        e.setStringValue("Hello World");
        em.persist(e);

        return redirect(go(TestCrudController.class).browse(
                TestCrudEntityA.class, null));
    }
}
