package com.github.ruediste.rise.component.components;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.component.fragment.HtmlFragment;
import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ValidationStatus;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.collect.Iterables;

public class CFormGroupTemplate extends BootstrapComponentTemplateBase<CFormGroup> {

    @Inject
    LabelUtil labelUtil;

    @Inject
    ValidationUtil validationUtil;

    @Override
    public void doRender(CFormGroup component, BootstrapRiseCanvas<?> html) {
        HtmlFragment contentFragment = html.toFragmentAndAdd(() -> html.render(component.getContent()));

        html.direct(() -> {
            Optional<? extends LString> label = component.getLabel();
            if (!label.isPresent()) {
                List<LString> labels = contentFragment.getLabels();
                if (labels.size() == 1)
                    label = Optional.of(labels.get(0));
            }

            ValidationStatus violationStatus = validationUtil.getValidationState(contentFragment);
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
            label.ifPresent(l -> html.bControlLabel().content(l));

            html.render(contentFragment.getHtmlProducer(), true);

            // render constraint violation
            if (violationStatus != null) {
                Collection<ValidationFailure> constraintViolations = violationStatus.getFailures();
                if (constraintViolations != null && !constraintViolations.isEmpty()) {
                    if (constraintViolations.size() == 1) {
                        html.span().BhelpBlock().content(Iterables.getOnlyElement(constraintViolations).getMessage());
                    }
                    if (constraintViolations.size() > 1) {
                        html.ul().BhelpBlock().fForEach(constraintViolations, v -> html.li().content(v.getMessage()))
                                ._ul();
                    }
                }
            }
            html._bFormGroup();
        });
    }

}
