package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.fragment.HtmlFragment;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CReloadTemplate extends Html5ComponentTemplateBase<CReload> {
    @Inject
    ComponentUtil util;

    @Inject
    ComponentRequestInfo info;

    @Override
    public void doRender(CReload component, RiseCanvas<?> html) {
        if (info.isReloadRequest())
            html.render(component.getBody());
        else {
            HtmlFragment fragment = html.toFragment(component.getBody());
            html.form().CLASS("rise_reload").DATA("rise-fragmentnr", Long.toString(util.getFragmentNr(fragment)))
                    .rCOMPONENT_ATTRIBUTES(component).render(fragment)._form();
        }
    }

}
