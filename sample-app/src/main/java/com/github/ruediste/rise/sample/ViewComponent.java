package com.github.ruediste.rise.sample;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.Component;

public abstract class ViewComponent<TController> extends
        ViewComponentBase<TController> {

    protected Component render(Renderable<SampleCanvas> renderable) {

    }
}
