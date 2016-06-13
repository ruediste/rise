package com.github.ruediste.rise.component.components;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss;
import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonArgs;
import com.github.ruediste.rise.api.ButtonStyle;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CButtonTemplate extends BootstrapComponentTemplateBase<CButton> {
    @Inject
    ComponentUtil util;

    @Inject
    ComponentPage info;

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    public static Consumer<CButton> setArgs(Consumer<B_ButtonArgs<?>> args) {
        return button -> button.args(args);
    }

    private static class ButtonArgs extends B_ButtonArgs<ButtonArgs> {

        protected ButtonArgs(BootstrapCanvasCss<?> html, boolean isLink) {
            super(html, isLink);
        }

        @Override
        public ButtonArgs disabled() {
            throw new UnsupportedOperationException("use setDisabled() of CButton instead");
        }

        void disabled_internal() {
            super.disabled();
        }
    }

    @Override
    public void doRender(CButton button, BootstrapRiseCanvas<?> html) {
        boolean isLink = button.getTarget() != null;
        Supplier<B_ButtonArgs<?>> argSupplier = () -> {
            ButtonArgs argsInst = new ButtonArgs(html, isLink);
            Consumer<B_ButtonArgs<?>> args = button.getArgs();
            if (args != null)
                args.accept(argsInst);
            if (button.isDisabled())
                argsInst.disabled_internal();
            if (button.getInvokedMethod() != null) {
                ButtonStyle buttonStyle = button.getInvokedMethod().getAnnotation(ButtonStyle.class);
                if (buttonStyle != null)
                    argsInst.style(buttonStyle.value());
            }
            return argsInst;
        };

        if (isLink) {
            html.bButtonA(argSupplier).CLASS("rise_button_link").rCOMPONENT_ATTRIBUTES(button)
                    .fIf(button.isDisabled(), () -> html.HREF("#"), () -> html.HREF(button.getTarget()))
                    .render(button.getBody())._bButtonA();
        } else {
            html.bButton(argSupplier).CLASS("rise_button").rCOMPONENT_ATTRIBUTES(button).RonClick(button.getHandler())
                    .render(button.getBody())._bButton();
        }
    }

}
