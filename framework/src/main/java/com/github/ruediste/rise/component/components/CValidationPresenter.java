package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.render.ComponentState;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValidationStatus;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationPresenter;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CValidationPresenter extends Component<CValidationPresenter>implements ValidationPresenter {

    @ComponentState
    private ValidationStatus validationStatus = new ValidationStatus();

    private Renderable<RiseCanvas<?>> body;

    private boolean includeUnhandledPageFailures;

    public static class Template extends BootstrapComponentTemplateBase<CValidationPresenter> {
        @Inject
        LabelUtil labelUtil;

        @Inject
        ValidationUtil validationUtil;

        @Inject
        ComponentPage page;

        @Override
        public void doRender(CValidationPresenter component, BootstrapRiseCanvas<?> html) {
            html.addPlaceholder(() -> {

                if (component.isIncludeUnhandledPageFailures())
                    for (ValidationFailure failure : page.getUnhandledValidationFailures())
                        renderFailure(html, failure);
                for (ValidationFailure failure : component.getValidationStatus().failures) {
                    renderFailure(html, failure);
                }
            });
            html.render(component.getBody());
        }

        private void renderFailure(BootstrapRiseCanvas<?> html, ValidationFailure failure) {
            html.p().BbgDanger();
            failure.render(html);
            html._p();
        }

    }

    public CValidationPresenter() {
    }

    public CValidationPresenter(Component<?> body) {
        body(body);
    }

    public CValidationPresenter body(Component<?> body) {
        this.body = html -> html.add(body);
        return this;
    }

    public CValidationPresenter(Runnable body) {
        body(body);
    }

    @Override
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public Renderable<RiseCanvas<?>> getBody() {
        return body;
    }

    public void body(Runnable body) {
        this.body = html -> body.run();
    }

    public void body(Renderable<RiseCanvas<?>> body) {
        this.body = body;
    }

    public boolean isIncludeUnhandledPageFailures() {
        return includeUnhandledPageFailures;
    }

    public CValidationPresenter includeUnhandledPageFailures(boolean includeUnhandledPageFailures) {
        this.includeUnhandledPageFailures = includeUnhandledPageFailures;
        return this;
    }
}
