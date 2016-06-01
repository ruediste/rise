package com.github.ruediste.rise.integration;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rise.component.fragment.FragmentCanvasBase;

/**
 * Base class for {@link HtmlCanvas}es.
 * 
 * <p>
 * {@link #initialize(com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget)}
 * is called from post construct method, so no need to call it yourself. Data is
 * always written to a
 */
public abstract class RiseCanvasBase<TSelf extends RiseCanvasBase<TSelf>> extends FragmentCanvasBase<TSelf>
        implements RiseCanvas<TSelf> {

    @Inject
    RiseCanvasHelper helper;

    @Override
    public RiseCanvasHelper internal_riseHelper() {
        return helper;
    }

    public void flush() {
        internal_target().commitAttributes();
        internal_target().flush();
    }

}
