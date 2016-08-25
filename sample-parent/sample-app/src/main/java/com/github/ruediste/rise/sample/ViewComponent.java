package com.github.ruediste.rise.sample;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.FragmentCanvas;

/**
 * Base class for views for the component framework.
 */
public abstract class ViewComponent<TController extends SubControllerComponent> extends ViewComponentBase<TController> {

    @Override
    protected final void render(FragmentCanvas<?> html) {
        doRender((SampleCanvas) html);
    }

    protected abstract void doRender(SampleCanvas html);
}
