package com.github.ruediste.rise.component.components;

import java.util.Optional;

import com.github.ruediste.rise.component.fragment.HtmlFragment;
import com.github.ruediste.rise.component.fragment.ValueHandle;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CCheckBoxTemplate extends ComponentTemplateBase<CCheckBox> {

    @Override
    public void doRender(CCheckBox component, RiseCanvas<?> html) {
        ValueHandle<Boolean> handle = html.createValueHandle(component.getValue());
        HtmlFragment fragment = new HtmlFragment() {
            @Override
            public void applyValues() {
                handle.set(util.getParameterValue(this, "value").isPresent());
            }
        };
        html.addFragmentAndRender(fragment);
        html.input_checkbox().NAME(util.getParameterKey(fragment, "value")).VALUE("true").addAttributeOpt("CHECKED",
                () -> {
                    if (handle.get()) {
                        return Optional.of("CHECKED");
                    } else
                        return Optional.empty();
                });
    }

}
