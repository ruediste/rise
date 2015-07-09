package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.testApp.persistence.TestEntity;

public class CrudInvocationController extends
        ControllerMvc<CrudInvocationController> {

    public ActionResult browseTestEnties() {
        return redirect(go(TestCrudController.class).browse(TestEntity.class));
    }
}
