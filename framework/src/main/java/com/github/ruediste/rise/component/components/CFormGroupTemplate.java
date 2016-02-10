package com.github.ruediste.rise.component.components;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ViolationStatus;
import com.github.ruediste.rise.component.validation.ViolationStatusBearer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.collect.Iterables;

public class CFormGroupTemplate
        extends BootstrapComponentTemplateBase<CFormGroup> {

    @Inject
    LabelUtil labelUtil;

    @Override
    public void doRender(CFormGroup component, BootstrapRiseCanvas<?> html) {

        LString label = ComponentTreeUtil
                .componentOfTypeIfSingle(component, LabeledComponent.class)
                .map(x -> x.getLabel(labelUtil)).orElse(null);
        ViolationStatus violationStatus = ComponentTreeUtil
                .componentOfTypeIfSingle(component, ViolationStatusBearer.class)
                .map(x -> x.getViolationStatus()).orElse(null);

        html.bFormGroup().CLASS(component.CLASS());

        // render violation status
        if (violationStatus != null) {
            if (violationStatus
                    .getValidationState() == ValidationState.SUCCESS) {
                html.BhasSuccess();
            }

            if (violationStatus.getValidationState() == ValidationState.ERROR) {
                html.BhasError();
            }
        }

        // add label
        if (label != null)
            html.bControlLabel().FOR(util.getComponentId(component))
                    .content(label);

        // add children
        html.renderChildren(component);

        // render constraint violation
        if (violationStatus != null) {
            Collection<ConstraintViolation<?>> constraintViolations = violationStatus
                    .getConstraintViolations();
            if (constraintViolations != null
                    && !constraintViolations.isEmpty()) {
                if (constraintViolations.size() == 1) {
                    html.span().BhelpBlock().content(Iterables
                            .getOnlyElement(constraintViolations).getMessage());
                }
                if (constraintViolations.size() > 1) {
                    html.ul().BhelpBlock();
                    for (ConstraintViolation<?> v : constraintViolations) {
                        html.li().content(v.getMessage());
                    }
                    html._ul();
                }
            }
        }
        html._bFormGroup();
    }

}
