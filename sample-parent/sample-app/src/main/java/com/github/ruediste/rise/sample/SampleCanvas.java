package com.github.ruediste.rise.sample;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasBase;

/**
 * Html canvas for the sample application
 */
public class SampleCanvas extends RiseCanvasBase<SampleCanvas>
        implements BootstrapRiseCanvas<SampleCanvas> {

    @Override
    public SampleCanvas self() {
        return this;
    }

}
