package com.github.ruediste.rise.testApp;

import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvasBase;

public class TestCanvas extends RiseCanvasBase<TestCanvas> implements
        BootstrapRiseCanvas<TestCanvas> {

    @Override
    public TestCanvas self() {
        return this;
    }

}
