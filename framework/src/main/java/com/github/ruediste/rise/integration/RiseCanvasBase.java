package com.github.ruediste.rise.integration;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rise.component.render.RiseCanvasTarget;

/**
 * Base class for {@link HtmlCanvas}es.
 * 
 */
public abstract class RiseCanvasBase<TSelf extends RiseCanvasBase<TSelf>> implements RiseCanvas<TSelf> {

    @Inject
    RiseCanvasHelper helper;

    private RiseCanvasTarget target;

    @Override
    public RiseCanvasHelper internal_riseHelper() {
        return helper;
    }

    public void flush() {
        internal_target().commitAttributes();
        internal_target().flush();
    }

    @Override
    public RiseCanvasTarget internal_target() {
        return target;
    }

    public RiseCanvasTarget getTarget() {
        return target;
    }

    @Override
    public void setTarget(RiseCanvasTarget target) {
        this.target = target;
    }
}
