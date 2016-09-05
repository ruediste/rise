package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Component representing a partial page reload context
 */
public class CReload extends Component<CReload> {

    private int reloadCount;
    private final Runnable body;

    static class Template extends Html5ComponentTemplateBase<CReload> {
        @Inject
        ComponentUtil util;

        @Inject
        ComponentRequestInfo info;

        @Override
        public void doRender(CReload component, RiseCanvas<?> html) {
            if (info.isReloadRequest())
                component.getBody().run();
            else {
                html.form().CLASS("rise_reload").DATA("rise-fragmentnr", Long.toString(util.getFragmentNr(component)))
                        .rCOMPONENT_ATTRIBUTES(component).render(component.getBody())._form();
            }
        }

    }

    public CReload(Runnable body) {
        this.body = body;
    }

    public int getReloadCount() {
        return reloadCount;
    }

    public void setReloadCount(int reloadCount) {
        this.reloadCount = reloadCount;
    }

    public Runnable getBody() {
        return body;
    }

}
