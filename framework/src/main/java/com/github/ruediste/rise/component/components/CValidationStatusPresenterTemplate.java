package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ValidationStatus;
import com.github.ruediste.rise.component.validation.ValidationStatusRepository;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CValidationStatusPresenterTemplate extends BootstrapComponentTemplateBase<CValidationStatusPresenter> {

    @Inject
    ValidationStatusRepository repo;

    @Override
    public void doRender(CValidationStatusPresenter component, BootstrapRiseCanvas<?> html) {
        ValidationStatus violationStatus = repo.getValidationStatus(component);

        html.div();
        if (violationStatus != null && violationStatus.getState() == ValidationState.FAILED) {
            for (ValidationFailure failure : violationStatus.getFailures()) {
                html.div().CLASS("alert alert-danger").content(failure.getMessage());
            }
        }

        html.renderChildren(component)._div();
    }

}
