package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.ComponentBase;

/**
 * Component representing a partial page reload context
 */
@DefaultTemplate(CReloadTemplate.class)
public class CReload extends ComponentBase<CReload> {

    private int reloadCount;
    private final Runnable body;

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
