package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.integration.RiseCanvas;

public class CCLickEditTemplate extends ComponentTemplateBase<CClickEdit<?>> {

    @Override
    public void doRender(CClickEdit<?> component, RiseCanvas<?> html) {
        html.div().CLASS("rise_click_edit")
                .fIf(component.isEdit(), () -> html.CLASS("_edit"),
                        () -> html.CLASS("_view"))
                .fIf(component.isFocusEditComponentOnReloadAndClear()
                        && (component.getFocusComponent() != null),
                        () -> html.DATA("rise-click-edit-focus-on-reload",
                                String.valueOf(getComponentId(
                                        component.getFocusComponent()))))
                .rCOMPONENT_ATTRIBUTES(component).renderChildren(component)
                ._div();
    }

    @Override
    public void raiseEvents(CClickEdit<?> component) {
        if (isParameterDefined(component, "view"))
            component.switchToView();
        else if (isParameterDefined(component, "edit"))
            component.switchToEdit();

    }
}
