package com.github.ruediste.rise.component.components;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.render.ComponentState;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValidationStatus;
import com.github.ruediste.rise.component.validation.ValidationClassification;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationPresenter;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.collect.Iterables;

public class CFormGroup extends Component<CFormGroup>implements ValidationPresenter {

    private Optional<? extends LString> label = Optional.empty();
    private Renderable<? extends RiseCanvas<?>> content;

    @ComponentState
    private ValidationStatus validationStatus = new ValidationStatus();

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
                // render violation status
                ValidationStatus status = component.getValidationStatus();
                ValidationClassification classification = status.getClassification();
                if (classification == ValidationClassification.SUCCESS) {
                    html.BhasSuccess();
                }

                if (classification == ValidationClassification.FAILED) {
                    html.BhasError();
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
                Collection<ValidationFailure> constraintViolations = component.getValidationStatus().failures;
                if (constraintViolations != null && !constraintViolations.isEmpty()) {
                    if (constraintViolations.size() == 1) {
                        html.span().BhelpBlock();
                        Iterables.getOnlyElement(constraintViolations).render(html);
                        html._span();
                    }
                    if (constraintViolations.size() > 1) {
                        html.ul().BhelpBlock().fForEach(constraintViolations, v -> {
                            html.li();
                            v.render(html);
                            html._li();
                        })._ul();
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

    @Override
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }
}
