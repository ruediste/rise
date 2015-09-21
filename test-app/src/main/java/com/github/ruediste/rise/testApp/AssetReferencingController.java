package com.github.ruediste.rise.testApp;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;

public class AssetReferencingController
        extends ControllerMvc<AssetReferencingController> {

    public ActionResult index() {
        return view(AssetReferencingView.class, "Hello");
    }
}
