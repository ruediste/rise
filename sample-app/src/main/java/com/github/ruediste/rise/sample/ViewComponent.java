package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;

public abstract class ViewComponent<TController> extends
        ViewComponentBase<TController> {

    @Inject
    ComponentUtil util;

    protected Component toComponent(Renderable<SampleCanvas> renderable) {
        return util.toComponent(renderable);
    }
}
