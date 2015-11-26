package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CReloadTemplate extends Html5ComponentTemplateBase<CReload> {
    @Inject
    ComponentUtil util;

    @Inject
    ComponentRequestInfo info;

    @Override
    public void doRender(CReload component, RiseCanvas<?> html) {
        if (info.isReloadRequest())
            html.renderChildren(component);
        else
            html.form().CLASS("rise_reload").rCOMPONENT_ATTRIBUTES(component)
                    .renderChildren(component)._form();
    }

    @Override
    public void applyValues(CReload component) {
        component.setReloadCount(component.getReloadCount() + 1);
    }
}
