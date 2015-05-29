package com.github.ruediste.rise.component.components.template;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CLink;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CLinkHtmlTemplate extends Html5ComponentTemplateBase<CLink> {
    @Inject
    ComponentUtil util;

    @Override
    public void doRender(CLink component, RiseCanvas<?> html) {
        html.a().HREF(component.getDestination()).render(children(component))
                ._a();
    }
}
