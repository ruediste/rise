package com.github.ruediste.rise.component.components.template;

import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CControllerTemplate extends
        Html5ComponentTemplateBase<CController> {

    @Override
    public void doRender(CController component, RiseCanvas<?> html) {
        html.render(component(component));
    }

}
