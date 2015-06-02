package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.components.template.Html5ComponentTemplateBase;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CTextTemplate extends Html5ComponentTemplateBase<CText> {

    @Override
    public void doRender(CText component, RiseCanvas<?> html) {
        html.write(component.getText());
    }

}
