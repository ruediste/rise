package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CReloadHtmlTemplate extends Html5ComponentTemplateBase<CReload> {
    @Inject
    ComponentUtil util;

    @Inject
    ComponentRequestInfo info;

    @Override
    public void doRender(CReload component, RiseCanvas<?> html) {
        if (info.isReloadRequest())
            html.renderChildren(component);
        else
            html.form().CLASS("rise_reload")
                    .DATA(CoreAssetBundle.componentAttributeNr,
                            String.valueOf(util.getComponentNr(component)))
                    .renderChildren(component)._form();
    }

    @Override
    public void applyValues(CReload component) {
        component.setReloadCount(component.getReloadCount() + 1);
    }
}
