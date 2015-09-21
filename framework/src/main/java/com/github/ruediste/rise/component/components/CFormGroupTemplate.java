package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.ValidationState;
import com.github.ruediste.rise.component.ViolationStatus;
import com.github.ruediste.rise.component.ViolationStatusBearer;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
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

        LString label = null;
        ViolationStatus violationStatus = null;
        Collection<ConstraintViolation<?>> constraintViolations = null;
        {
            List<Component> subTree = ComponentTreeUtil.subTree(component);
            List<Component> labeled = subTree.stream()
                    .filter(x -> x instanceof LabeledComponent)
                    .collect(toList());
            if (labeled.size() == 1)
                label = ((LabeledComponent) labeled.get(0)).getLabel(labelUtil);
            List<Component> constraintAwares = subTree.stream()
                    .filter(x -> x instanceof ViolationStatusBearer)
                    .collect(toList());
            if (constraintAwares.size() == 1) {
                violationStatus = ((ViolationStatusBearer) constraintAwares
                        .get(0)).getViolationStatus();
                constraintViolations = violationStatus
                        .getConstraintViolations();
            }

        }

        html.bFormGroup().CLASS(component.CLASS());

        if (violationStatus != null) {
            if (violationStatus
                    .getValidationState() == ValidationState.SUCCESS) {
                html.BhasSuccess();
            }

            if (violationStatus.getValidationState() == ValidationState.ERROR) {
                html.BhasError();
            }
        }

        if (label != null)
            html.bControlLabel().FOR(util.getComponentId(component))
                    .content(label);

        html.renderChildren(component);

        if (constraintViolations != null && !constraintViolations.isEmpty()) {
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
        html._bFormGroup();
    }

}
