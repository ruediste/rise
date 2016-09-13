package com.github.ruediste.rise.testApp.component;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.testApp.TestCanvas;

public abstract class ViewComponent<TController extends SubControllerComponent> extends ViewComponentBase<TController> {

    @Inject
    Provider<TestCanvas> canvasProvider;

    @Override
    public void render(RiseCanvas<?> html) {
        renderImpl((TestCanvas) html);
    }

    protected abstract void renderImpl(TestCanvas html);
}
