package com.github.ruediste.rise.component.components.template;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CButtonHtmlTemplate extends Html5ComponentTemplateBase<CButton> {
    @Inject
    ComponentUtil util;

    @Override
    public void doRender(CButton component, RiseCanvas<?> html) {
        html.button()
                .CLASS(util.combineCssClasses("rise_button", component.CLASS()))
                .DATA(CoreAssetBundle.componentAttributeNr,
                        String.valueOf(util.getComponentNr(component)))
                .render(components(component.getChildren()))._button();
    }

    @Override
    public void raiseEvents(CButton component) {
        if (util.isParameterDefined(component, "clicked")
                && component.getHandler() != null) {
            component.getHandler().run();
        }
    }

}
