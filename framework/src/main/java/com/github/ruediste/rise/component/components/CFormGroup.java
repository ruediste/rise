package com.github.ruediste.rise.component.components;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ValidationClassification;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.collect.Iterables;

public class CFormGroup extends Component<CFormGroup> {

    private Optional<? extends LString> label = Optional.empty();
    private Renderable<? extends RiseCanvas<?>> content;

    static class Template extends BootstrapComponentTemplateBase<CFormGroup> {

        @Inject
        LabelUtil labelUtil;

        @Inject
        ValidationUtil validationUtil;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void doRender(CFormGroup component, BootstrapRiseCanvas<?> html) {

            html.bFormGroup().CLASS(component.CLASS());

            html.addAttributePlaceholder(() -> {
                ValidationClassification violationStatus = validationUtil.getValidationState(component);
                // render violation status
                if (violationStatus != null) {
                    if (violationStatus.getState() == ValidationState.SUCCESS) {
                        html.BhasSuccess();
                    }

                    if (violationStatus.getState() == ValidationState.FAILED) {
                        html.BhasError();
                    }
                }
            });

            // add label
            html.addPlaceholder(() -> {
                Optional<? extends LString> label = component.getLabel();
                if (!label.isPresent()) {
                    List<LString> labels = component.getLabels();
                    if (labels.size() == 1)
                        label = Optional.of(labels.get(0));
                }
                label.ifPresent(l -> html.bControlLabel().content(l));
            });

            html.render((Renderable) component.content);

            // render constraint violation
            html.addPlaceholder(() -> {
                ValidationClassification violationStatus = validationUtil.getValidationState(component);
                if (violationStatus != null) {
                    Collection<ValidationFailure> constraintViolations = violationStatus.getFailures();
                    if (constraintViolations != null && !constraintViolations.isEmpty()) {
                        if (constraintViolations.size() == 1) {
                            html.span().BhelpBlock()
                                    .content(Iterables.getOnlyElement(constraintViolations).getMessage());
                        }
                        if (constraintViolations.size() > 1) {
                            html.ul().BhelpBlock()
                                    .fForEach(constraintViolations, v -> html.li().content(v.getMessage()))._ul();
                        }
                    }
                }
            });
            html._bFormGroup();
        }

    }

    public CFormGroup(Runnable content) {
        this.content = html -> content.run();
    }

    public CFormGroup(Component<?> content) {
        this.content = html -> html.add(content);
    }

    public Optional<? extends LString> getLabel() {
        return label;
    }

    public CFormGroup setLabel(Optional<? extends LString> label) {
        this.label = label;
        return this;
    }

    public Renderable<? extends RiseCanvas<?>> getContent() {
        return content;
    }
}
