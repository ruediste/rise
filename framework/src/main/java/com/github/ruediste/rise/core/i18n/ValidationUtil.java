package com.github.ruediste.rise.core.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.binding.BindingInfo;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValidationStatus;
import com.github.ruediste.rise.component.validation.ValidationClassification;
import com.github.ruediste.rise.component.validation.ValidationPathUtil;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.github.ruediste1.i18n.label.LabelUtil;

/**
 * Utility for {@link Component} validation.
 * 
 * <p>
 * Validation happens both on the model and in the components.
 * ValidationFailures of the model are pulled up via the bindings into the
 * {@link ValidationPresenter} components. Validation failures of the components
 * are pushed down to the controller.
 * 
 * <p>
 * After rendering the validation failures are pulled up from the controllers to
 * the components. This is accomplished by examining the bindings of each
 * component and locating matching {@link ValidationFailure}s. The failures are
 * added to the closest ancestor implementing {@link ValidationPresenter}. If no
 * presenter can be found, the failure is added to the view. All failures
 * present on a rendered controller which are not correlated to a component are
 * also added to the view.
 * 
 * <p>
 * The
 * <p>
 * The validation failures are then added to the server response while replacing
 * placeholders that were added while rendering.
 * 
 * <p>
 * <img src="doc-files/ValidationOverview.png" >
 */
public class ValidationUtil {
    @Inject
    PatternStringResolver patternStringResolver;

    @Inject
    TranslatedStringResolver translatedStringResolver;

    @Inject
    LabelUtil labelUtil;

    @Inject
    ValidationPathUtil validationPathUtil;

    @Inject
    ComponentPage page;

    /**
     * Calculate a localized string message for a constraint violation.
     * <p>
     * If a labeled payload is present, the label of the payload is used as
     * template. Although not required, you are encouraged to use
     * {@link ValidationMessage} as base class for such payloads. If no labeled
     * payload is present and the message has the form "{......}", the string
     * without the brackets is used as key and the corresponding resource string
     * is used as template. Otherwise, the message is used directly as template.
     * 
     * <p>
     * When the template is determined, it is used to create a
     * {@link PatternString} . The arguments are the standard validation
     * arguments (all attributes of the constraint annotation) plus
     * "invalidValue" containing the value which has been validated.
     * 
     * <p>
     * finally the PatternString is resolved and returned as message.
     * 
     */
    public LString getMessage(ConstraintViolation<?> violation) {

        // arguments for the message
        Map<String, Object> args = new HashMap<>();
        args.putAll(violation.getConstraintDescriptor().getAttributes());
        args.put("invalidValue", violation.getInvalidValue());

        // check for labeled payload
        {
            Optional<TranslatedString> pattern = violation.getConstraintDescriptor().getPayload().stream()
                    .filter(Payload.class::isAssignableFrom).map(x -> labelUtil.type(x).tryLabel())
                    .filter(x -> x.isPresent()).map(x -> x.get()).findFirst();
            if (pattern.isPresent())
                // we found a labeled payload. Use payload label as pattern
                return new PatternString(patternStringResolver, pattern.get(), args);
        }

        // check for resource key reference
        String messageTemplate = violation.getMessageTemplate();
        if (messageTemplate.startsWith("{") && messageTemplate.endsWith("}")) {
            return new PatternString(patternStringResolver, new TranslatedString(translatedStringResolver,
                    messageTemplate.substring(1, messageTemplate.length() - 1)), args);
        }

        // fallback: just use the string as is as pattern
        return new PatternString(patternStringResolver, LString.of(messageTemplate), args);
    }

    public List<ValidationFailure> toFailures(Iterable<? extends ConstraintViolation<?>> violations) {
        return toFailures(violations, ValidationFailureSeverity.ERROR);
    }

    public List<ValidationFailure> toFailures(Iterable<? extends ConstraintViolation<?>> violations,
            ValidationFailureSeverity severity) {
        if (violations == null)
            return Collections.emptyList();
        return StreamSupport.stream(violations.spliterator(), false)
                .<ValidationFailure> map(v -> toFailure(v, severity)).collect(Collectors.toList());
    }

    public ValidationFailure toFailure(ConstraintViolation<?> violation) {
        return toFailure(violation, ValidationFailureSeverity.ERROR);
    }

    public ValidationFailure toFailure(ConstraintViolation<?> violation, ValidationFailureSeverity severity) {
        LString message = getMessage(violation);
        return new ValidationFailure() {

            @Override
            public ValidationFailureSeverity getSeverity() {
                return severity;
            }

            @Override
            public LString getMessage() {
                return message;
            }

            @Override
            public String toString() {
                return "ValidationFailure(" + severity + ";" + message + ")";
            }
        };
    }

    public void performValidation() {
        performValidation(page.getRoot());
    }

    /**
     * Perform the validation on a whole page
     */
    public void performValidation(Component<?> pageRoot) {
        // clear all validation state
        pageRoot.forSubTree(x -> {
            if (x instanceof ValidationPresenter) {
                ValidationStatus status = ((ValidationPresenter) x).getValidationStatus();
                status.failures.clear();
                status.isValidated = false;
            }
            ViewComponentBase<?> view = x.getView();
            SubControllerComponent ctrl = view.getController();
            view.validationStatus.clear();
            ctrl.validationClassification = ValidationClassification.NOT_VALIDATED;
        });

        // build helper data structures for easy access to required objects
        List<Component<?>> rootComponents = new ArrayList<>();
        List<Component<?>> components = new ArrayList<>();
        List<ValidationPresenter> validationPresenters = new ArrayList<>();
        Set<ViewComponentBase<?>> views = new HashSet<>();
        Set<SubControllerComponent> controllers = new HashSet<>();
        pageRoot.forSubTreePartial(rootComponent -> {
            if (rootComponent.getView().getController().validateView) {
                // handle the root components which have a controller requesting
                // validation
                rootComponents.add(rootComponent);
                rootComponent.forSubTree(component -> {
                    components.add(component);
                    if (component instanceof ValidationPresenter)
                        validationPresenters.add((ValidationPresenter) component);
                    views.add(component.getView());
                    controllers.add(component.getView().getController());
                });

                // don't dive into subtree
                return false;
            }
            return true;
        });

        Map<SubControllerComponent, List<SubControllerComponent>> controllerAncestors = new HashMap<>();
        rootComponents.forEach(x -> fillControllerAncestors(controllerAncestors, x, Collections.emptyList()));

        // initialize validation of controllers
        controllers.forEach(ctrl -> ctrl.validationClassification = ValidationClassification.SUCCESS);

        // mark validationPresenters as validated
        validationPresenters.forEach(x -> x.getValidationStatus().isValidated = true);

        // mark views as validated
        views.forEach(v -> v.validationStatus.isValidated = true);

        Set<ValidationFailure> handledControllerFailures = new HashSet<>();

        // pull failures up from controllers into components and handle
        // component validation failures
        for (Component<?> component : components) {
            ArrayList<ValidationFailure> failures = new ArrayList<>();

            // pull up validation failures from the controllers
            SubControllerComponent ctrl = component.getView().getController();
            for (BindingInfo<?> bindingInfo : component.getBindingInfos()) {
                Object owner = bindingInfo.propertyOwnerSupplier.get();
                failures.addAll(ctrl.validationFailureMap.get(Pair.of(owner, bindingInfo.modelProperty.getName())));
            }
            handledControllerFailures.addAll(failures);

            // validate component
            {
                List<ValidationFailure> componentFailures = component.validate();
                if (!componentFailures.isEmpty())
                    // failures originating from the controller itself are
                    // handled below
                    markControllerValidationAsFailed(ctrl, controllerAncestors);
                failures.addAll(componentFailures);
            }

            if (!failures.isEmpty()) {
                // add failures to presenter
                boolean found = false;
                for (Component<?> ancestor : component.parents()) {
                    if (ancestor instanceof ValidationPresenter) {
                        ((ValidationPresenter) ancestor).getValidationStatus().failures.addAll(failures);
                        found = true;
                        break;
                    }
                }

                // fallback to the view
                if (!found)
                    component.getView().validationStatus.addFailures(failures);
            }
        }

        // pull remaining failures from controllers into view
        for (ViewComponentBase<?> view : views) {
            SubControllerComponent controller = view.getController();
            if (!controller.validationFailureMap.isEmpty()) {
                markControllerValidationAsFailed(controller, controllerAncestors);
                controller.validationFailureMap.values().stream().filter(x -> !handledControllerFailures.contains(x))
                        .forEach(view.validationStatus::addFailure);
            }
        }

    }

    private void markControllerValidationAsFailed(SubControllerComponent ctrl,
            Map<SubControllerComponent, List<SubControllerComponent>> controllerAncestors) {
        ctrl.validationClassification = ValidationClassification.FAILED;
        for (SubControllerComponent ancestor : controllerAncestors.get(ctrl)) {
            if (ancestor.validationClassification == ValidationClassification.FAILED)
                break;
            ancestor.validationClassification = ValidationClassification.FAILED;
        }
    }

    private void fillControllerAncestors(Map<SubControllerComponent, List<SubControllerComponent>> map,
            Component<?> component, List<SubControllerComponent> ancestors) {
        SubControllerComponent parentController = component.getView().getController();
        map.put(parentController, ancestors);
        for (Component<?> child : component.getChildren()) {
            SubControllerComponent childController = child.getView().getController();
            List<SubControllerComponent> childAncestors;
            if (childController != parentController) {
                childAncestors = new ArrayList<>();
                childAncestors.add(childController);
                childAncestors.addAll(ancestors);
            } else
                childAncestors = ancestors;
            fillControllerAncestors(map, child, childAncestors);
        }
    }

    /**
     * Perform validation on a subtree of a page.
     * 
     * @param root
     *            com
     */
    void performSubtreeValidation(Component<?> root) {

    }

}
