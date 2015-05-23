package com.github.ruediste.rise.component.components;

import org.rendersnake.Renderable;

import com.github.ruediste.rise.component.components.template.CTagTemplate;

@DefaultTemplate(CTagTemplate.class)
public class CTag extends MultiChildrenComponent<CTag> {
    private Renderable tagRenderer;

    public CTag(Renderable tagRenderer) {
        this.setTagRenderer(tagRenderer);
    }

    public Renderable getTagRenderer() {
        return tagRenderer;
    }

    public void setTagRenderer(Renderable tagRenderer) {
        this.tagRenderer = tagRenderer;
    }
}
