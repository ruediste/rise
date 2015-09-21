package com.github.ruediste.rise.integration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvas;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasBase;
import com.google.common.base.Charsets;

/**
 * Base class for {@link HtmlCanvas}es.
 * 
 * <p>
 * {@link #initialize(com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget)}
 * is called from post construct method, so no need to call it yourself. Data is
 * always written to a
 */
public abstract class RiseCanvasBase<TSelf extends RiseCanvasBase<TSelf>>
        extends HtmlCanvasBase<TSelf>implements RiseCanvas<TSelf> {

    @Inject
    RiseCanvasHelper helper;

    public void initializeForComponent(ByteArrayOutputStream baos) {
        super.initialize(new OutputStreamWriter(baos, Charsets.UTF_8));
        helper.initializeForComponent(baos, internal_target());
    }

    public void initializeForOutput(ByteArrayOutputStream baos) {
        super.initialize(new OutputStreamWriter(baos, Charsets.UTF_8));
        helper.initializeForOutput(baos, internal_target());
    }

    @Override
    public RiseCanvasHelper internal_riseHelper() {
        return helper;
    }

    public void flush() {
        internal_target().commitAttributes();
        if (helper.isComponent())
            helper.commitBuffer();
        internal_target().flush();
    }

}
