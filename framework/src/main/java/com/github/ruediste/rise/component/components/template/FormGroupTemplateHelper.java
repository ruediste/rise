package com.github.ruediste.rise.component.components.template;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.ValidationState;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.collect.Iterables;

public class FormGroupTemplateHelper {

    @Inject
    ComponentUtil util;

    @Inject
    LabelUtil labelUtil;

    final public void renderFormGroup(CFormGroup<?> component,
            BootstrapRiseCanvas<?> html, Runnable innerRenderer) {
        html.bFormGroup().CLASS(component.CLASS());

        if (component.getValidationState() == ValidationState.SUCCESS) {
            html.B_HAS_SUCCESS();
        }

        if (component.getValidationState() == ValidationState.ERROR) {
            html.B_HAS_ERROR();
        }

        LString label = component.getLabel();
        if (label == null && component.getLabelProperty() != null) {
            label = labelUtil.getPropertyLabel(component.getLabelProperty());
        }
        if (label == null) {
            label = locale -> "";
        }

        html.bControlLabel().FOR(util.getComponentId(component)).content(label);

        innerRenderer.run();

        if (!component.getConstraintViolations().isEmpty()) {
            if (component.getConstraintViolations().size() == 1) {
                html.span()
                        .B_HELP_BLOCK()
                        .content(
                                Iterables.getOnlyElement(
                                        component.getConstraintViolations())
                                        .getMessage());
            }
            if (component.getConstraintViolations().size() > 1) {
                html.ul().B_HELP_BLOCK();
                for (ConstraintViolation<?> v : component
                        .getConstraintViolations()) {
                    html.li().content(v.getMessage());
                }
                html._ul();
            }
        }
        html._bFormGroup();
    }
}
