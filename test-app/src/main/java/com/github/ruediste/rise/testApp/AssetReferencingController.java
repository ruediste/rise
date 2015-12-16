package com.github.ruediste.rise.testApp;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;

public class AssetReferencingController
        extends ControllerMvc<AssetReferencingController> {

    @UrlUnsigned
    public ActionResult index() {
        return view(AssetReferencingView.class, "Hello");
    }
}
