package com.github.ruediste.rise.component.components;


/**
 * Component representing a partial page reload context
 */
@DefaultTemplate(CReloadHtmlTemplate.class)
public class CReload extends MultiChildrenComponent<CReload> {

    private int reloadCount;

    public int getReloadCount() {
        return reloadCount;
    }

    public void setReloadCount(int reloadCount) {
        this.reloadCount = reloadCount;
    }
}
