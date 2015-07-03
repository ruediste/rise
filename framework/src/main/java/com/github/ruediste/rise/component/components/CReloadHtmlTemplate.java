package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CReloadHtmlTemplate extends Html5ComponentTemplateBase<CReload> {
    @Inject
    ComponentUtil util;

    @Override
    public void doRender(CReload component, RiseCanvas<?> html) {
        html.form()
                .CLASS("rise_reload")
                .DATA(CoreAssetBundle.componentAttributeNr,
                        String.valueOf(util.getComponentNr(component)))
                .DATA("lwf-reload-count",
                        String.valueOf(component.getReloadCount()))
                .render(children(component))._form();
    }

    @Override
    public void applyValues(CReload component) {
        component.setReloadCount(component.getReloadCount() + 1);
    }
}
