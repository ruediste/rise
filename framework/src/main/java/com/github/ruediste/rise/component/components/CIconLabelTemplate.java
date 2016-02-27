package com.github.ruediste.rise.component.components;

import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CIconLabelTemplate
        extends BootstrapComponentTemplateBase<CIconLabel> {

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    @Override
    public void doRender(CIconLabel component, BootstrapRiseCanvas<?> html) {

        LString label = component.getLabel();
        if (label == null && component.getMethod() != null) {
            label = labelUtil.method(component.getMethod()).label();
        }

        if (label == null)
            throw new RuntimeException(
                    "Neither label nor method defined on CIconLabel");

        Optional<Renderable<Html5Canvas<?>>> icon = Optional
                .ofNullable(component.getIcon());
        if (!icon.isPresent())
            icon = iconUtil.tryGetIcon(component.getMethod());

        if (component.isShowIconOnly()) {
            html.render(icon.orElseThrow(
                    () -> new RuntimeException("Icon is not defined"))).span()
                    .BsrOnly().content(label);
        } else {
            html.fIfPresent(icon, html::render).write(label);
        }
    }

}
