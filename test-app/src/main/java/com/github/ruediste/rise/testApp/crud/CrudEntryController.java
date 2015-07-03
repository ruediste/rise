package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;

public class CrudEntryController extends ControllerMvc<CrudEntryController> {

    public ActionResult openBrowser(){
        return redirect(null)
    }
}
