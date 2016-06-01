package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.fragment.HtmlFragment;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CControllerTemplate extends BootstrapComponentTemplateBase<CController> {

    @Override
    public void doRender(CController component, BootstrapRiseCanvas<?> html) {
        HtmlFragment rootFragment = component.getView().getRootFragment();
        rootFragment.setParent(html.internal_target().getParentFragment());
        html.render(rootFragment);
    }

}
