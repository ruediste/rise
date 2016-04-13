package com.github.ruediste.rise.component.components;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ValidationStatus;
import com.github.ruediste.rise.component.validation.ValidationStatusRepository;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.collect.Iterables;

public class CFormGroupTemplate extends BootstrapComponentTemplateBase<CFormGroup> {

    @Inject
    LabelUtil labelUtil;

    @Inject
    ValidationStatusRepository repo;

    @Override
    public void doRender(CFormGroup component, BootstrapRiseCanvas<?> html) {

        Optional<? extends LString> label = component.getLabel();
        if (!label.isPresent()) {
            label = ComponentTreeUtil.componentOfTypeIfSingle(component, LabeledComponent.class)
                    .map(x -> x.getLabel(labelUtil));
        }
        ValidationStatus violationStatus = repo.getValidationStatus(component);

        html.bFormGroup().CLASS(component.CLASS());

        // render violation status
        if (violationStatus != null) {
            if (violationStatus.getState() == ValidationState.SUCCESS) {
                html.BhasSuccess();
            }

            if (violationStatus.getState() == ValidationState.FAILED) {
                html.BhasError();
            }
        }

        // add label
        label.ifPresent(l -> html.bControlLabel().FOR(util.getComponentId(component)).content(l));

        // add children
        html.renderChildren(component);

        // render constraint violation
        if (violationStatus != null) {
            Collection<ValidationFailure> constraintViolations = violationStatus.getFailures();
            if (constraintViolations != null && !constraintViolations.isEmpty()) {
                if (constraintViolations.size() == 1) {
                    html.span().BhelpBlock().content(Iterables.getOnlyElement(constraintViolations).getMessage());
                }
                if (constraintViolations.size() > 1) {
                    html.ul().BhelpBlock().fForEach(constraintViolations, v -> html.li().content(v.getMessage()))._ul();
                }
            }
        }
        html._bFormGroup();
    }

}
