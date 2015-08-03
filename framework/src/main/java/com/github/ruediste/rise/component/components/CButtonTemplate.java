package com.github.ruediste.rise.component.components;

import java.util.function.Consumer;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonArgs;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CButtonTemplate extends BootstrapComponentTemplateBase<CButton> {
    @Inject
    ComponentUtil util;

    @Inject
    PageInfo info;

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    public static AttachedProperty<CButton, Consumer<B_ButtonArgs>> args = new AttachedProperty<>(
            "buttonCustomizer");

    public static Consumer<CButton> setArgs(Consumer<B_ButtonArgs> args) {
        return button -> CButtonTemplate.args.set(button, args);
    }

    @Override
    public void doRender(CButton button, BootstrapRiseCanvas<?> html) {
        if (button.getTarget() != null) {
            html.bButtonA(args.get(button)).CLASS("rise_button_link")
                    .CLASS(button.CLASS()).TEST_NAME(button.TEST_NAME())
                    .HREF(button.getTarget()).renderChildren(button)
                    ._bButtonA();
        } else {
            html.bButton(args.get(button))
                    .CLASS("rise_button")
                    .CLASS(button.CLASS())
                    .TEST_NAME(button.TEST_NAME())
                    .DATA(CoreAssetBundle.componentAttributeNr,
                            String.valueOf(util.getComponentNr(button)))
                    .renderChildren(button)._bButton();
        }
    }

    @Override
    public void raiseEvents(CButton component) {
        if (util.isParameterDefined(component, "clicked")
                && component.getHandler() != null) {
            component.getHandler().run();
        }
    }

}
