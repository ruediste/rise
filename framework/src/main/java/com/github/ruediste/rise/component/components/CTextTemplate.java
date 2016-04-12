package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.lString.LString;

public class CTextTemplate extends Html5ComponentTemplateBase<CText> {

    @Override
    public void doRender(CText component, RiseCanvas<?> html) {
        LString text = component.getText();
        if (text == null)
            text = LString.empty();
        html.span().CLASS("rise_ctext").rCOMPONENT_ATTRIBUTES(component).content(text);
    }

}
