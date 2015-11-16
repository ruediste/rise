package com.github.ruediste.rise.integration;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.web.UrlSpec;

public abstract class StageRibbonControllerBase<TSelf extends StageRibbonControllerBase<TSelf>>
        extends ControllerMvc<TSelf> {
    public abstract ActionResult index(UrlSpec returnUrl);
}
