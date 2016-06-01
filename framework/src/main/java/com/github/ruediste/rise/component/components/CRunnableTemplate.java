package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CRunnableTemplate extends ComponentTemplateBase<CRunnable> {

    @Override
    public void doRender(CRunnable component, RiseCanvas<?> html) {
        html.render(component.getRunnable());
    }
}
