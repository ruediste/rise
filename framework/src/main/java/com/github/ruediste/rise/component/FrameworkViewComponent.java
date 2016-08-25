package com.github.ruediste.rise.component;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.FragmentCanvas;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

/**
 * {@link ViewComponentBase} implementation used for component views provided by
 * the framework.
 */
public abstract class FrameworkViewComponent<TController extends SubControllerComponent>
        extends ViewComponentBase<TController> {

    @Override
    final protected void render(FragmentCanvas<?> html) {
        renderImpl((BootstrapRiseCanvas<?>) html);
    }

    protected abstract void renderImpl(BootstrapRiseCanvas<?> html);

}