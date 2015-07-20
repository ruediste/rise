package com.github.ruediste.rise.component.components;

import java.lang.reflect.Method;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CButtonHtmlTemplate extends
        BootstrapComponentTemplateBase<CButton> {
    @Inject
    ComponentUtil util;

    @Inject
    PageInfo info;

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    @Override
    public void doRender(CButton button, BootstrapRiseCanvas<?> html) {
        html.bButton(button.getArgs())
                .CLASS("rise_button")
                .CLASS(button.CLASS())
                .DATA(CoreAssetBundle.componentAttributeNr,
                        String.valueOf(util.getComponentNr(button)));
        Method method = button.getInvokedMethod();
        if (method != null) {
            TranslatedString label = labelUtil.getMethodLabel(method);

            if (button.isIconOnly())
                html.TITLE(label);

            iconUtil.tryGetIcon(method).ifPresent(html::render);
            if (!button.isIconOnly())
                html.write(label);
        } else
            html.renderChildren(button);
        html._bButton();
    }

    @Override
    public void raiseEvents(CButton component) {
        if (util.isParameterDefined(component, "clicked")
                && component.getHandler() != null) {
            component.getHandler().run();
        }
    }

}
