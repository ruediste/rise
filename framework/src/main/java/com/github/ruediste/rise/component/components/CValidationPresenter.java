package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValidationStatus;
import com.github.ruediste.rise.component.validation.ValidationClassification;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationPresenter;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CValidationPresenter extends Component<CValidationPresenter>implements ValidationPresenter {

    private ValidationStatus validationStatus = new ValidationStatus();
    private Renderable<RiseCanvas<?>> body;

    public static class Template extends BootstrapComponentTemplateBase<CValidationPresenter> {
        @Inject
        LabelUtil labelUtil;

        @Inject
        ValidationUtil validationUtil;

        @Override
        public void doRender(CValidationPresenter component, BootstrapRiseCanvas<?> html) {
            html.addPlaceholder(() -> {
                ValidationStatus status = component.getValidationStatus();
                if (status.getClassification() == ValidationClassification.FAILED) {
                    for (ValidationFailure failure : component.getValidationStatus().failures) {
                        html.p().BhasError().content(failure.getMessage());

                    }
                }
            });
            html.render(component.getBody());
        }

    }

    public CValidationPresenter(Component<?> body) {
        this.body = html -> html.add(body);
    }

    public CValidationPresenter(Runnable body) {
        setBody(body);
    }

    @Override
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public Renderable<RiseCanvas<?>> getBody() {
        return body;
    }

    public void setBody(Runnable body) {
        this.body = html -> body.run();
    }

    public void setBody(Renderable<RiseCanvas<?>> body) {
        this.body = body;
    }
}
