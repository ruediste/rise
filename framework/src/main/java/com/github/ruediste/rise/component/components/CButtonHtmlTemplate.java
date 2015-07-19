package com.github.ruediste.rise.component.components;

import java.lang.reflect.Method;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CButtonHtmlTemplate extends Html5ComponentTemplateBase<CButton> {
    @Inject
    ComponentUtil util;

    @Inject
    PageInfo info;

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    @Override
    public void doRender(CButton button, RiseCanvas<?> html) {
        html.button()
                .CLASS(util.combineCssClasses("rise_button", button.CLASS()))
                .DATA(CoreAssetBundle.componentAttributeNr,
                        String.valueOf(util.getComponentNr(button)));
        Method method = button.getInvokedMethod();
        if (method != null) {
            iconUtil.tryGetIcon(method).ifPresent(html::render);
            html.write(labelUtil.getMethodLabel(method));
        } else
            html.renderChildren(button);
        html._button();
    }

    @Override
    public void raiseEvents(CButton component) {
        if (util.isParameterDefined(component, "clicked")
                && component.getHandler() != null) {
            component.getHandler().run();
        }
    }

}
