package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasBase;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasHelper;

public class SampleCanvas extends HtmlCanvasBase<SampleCanvas> implements
        BootstrapRiseCanvas<SampleCanvas> {

    @Inject
    RiseCanvasHelper helper;

    @Override
    public SampleCanvas self() {
        return this;
    }

    @Override
    public RiseCanvasHelper internal_riseHelper() {
        return helper;
    }

}
