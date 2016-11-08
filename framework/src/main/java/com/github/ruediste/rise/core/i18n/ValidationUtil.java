package com.github.ruediste.rise.core.i18n;

import static java.util.stream.Collectors.toList;

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
import javax.validation.Validator;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.SubControllerComponent.ValidationFailureCollector;
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
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

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
 * bubbled up starting at the root component of the view.
 * 
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

    @Inject
    Validator validator;

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

    private class Collector implements ValidationFailureCollector {

        public final Multimap<Pair<Object, String>, ValidationFailure> validationFailureMap = MultimapBuilder.hashKeys()
                .arrayListValues().build();

        @Override
        public void addConstraintViolations(Set<? extends ConstraintViolation<?>> violations) {
            for (ConstraintViolation<?> v : violations) {
                Object bean = v.getLeafBean();
                String property = ValidationPathUtil.getProperty(v.getPropertyPath());
                ValidationFailure failure = toFailure(v);
                addFailure(bean, property, failure);
            }

        }

        @Override
        public void addFailure(Object bean, String property, ValidationFailure failure) {
            validationFailureMap.put(Pair.of(bean, property), failure);
        }

        @Override
        public void validate(Object target, Class<?>... groups) {
            Set<ConstraintViolation<Object>> violations = validator.validate(target, groups);
            addConstraintViolations(violations);
        }

    }

    /**
     * Perform the validation on a whole page
     */
    public void performValidation() {
        Component<?> pageRoot = page.getRoot();
        // clear all validation state
        clearValidation();

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
                rootComponent.forSubTreePartial(component -> {
                    components.add(component);
                    if (component instanceof ValidationPresenter) {
                        if (!((ValidationPresenter) component).getValidationStatus().isOutputSuspended)
                            validationPresenters.add((ValidationPresenter) component);
                    }
                    views.add(component.getView());
                    controllers.add(component.getView().getController());
                    return true;
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

        // calculate controller failures
        Map<SubControllerComponent, Collector> collectors = new HashMap<>();
        controllers.forEach(ctrl -> {
            Collector collector = new Collector();
            ctrl.performValidation(collector);
            collectors.put(ctrl, collector);
        });

        Set<ValidationFailure> handledControllerFailures = new HashSet<>();

        // pull failures up from controllers into components and handle
        // component validation failures
        for (Component<?> component : components) {
            ArrayList<ValidationFailure> failures = new ArrayList<>();

            // pull up validation failures from the controllers
            SubControllerComponent ctrl = component.getView().getController();
            for (BindingInfo<?> bindingInfo : component.getBindingInfos()) {
                Object owner = bindingInfo.propertyOwnerSupplier.get();
                failures.addAll(collectors.get(ctrl).validationFailureMap
                        .get(Pair.of(owner, bindingInfo.modelProperty.getName())));
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

            addFailuresToNearestPresenter(component, failures);
        }

        // pull remaining failures from controllers into view
        for (ViewComponentBase<?> view : views) {
            if (view.parentComponent == null)
                continue;
            SubControllerComponent controller = view.getController();
            Collector collector = collectors.get(controller);
            if (!collector.validationFailureMap.isEmpty()) {
                markControllerValidationAsFailed(controller, controllerAncestors);
                List<ValidationFailure> unhandledFailures = collector.validationFailureMap.values().stream()
                        .filter(x -> !handledControllerFailures.contains(x)).collect(toList());
                if (!unhandledFailures.isEmpty())
                    addFailuresToNearestPresenter(view.parentComponent, unhandledFailures);
            }
        }

    }

    private void addFailuresToNearestPresenter(Component<?> component, List<ValidationFailure> failures) {
        if (!failures.isEmpty()) {
            // add failures to presenter
            for (Component<?> ancestor : component.parents()) {
                if (ancestor instanceof ValidationPresenter) {
                    ValidationStatus status = ((ValidationPresenter) ancestor).getValidationStatus();
                    if (!status.isOutputSuspended)
                        status.failures.addAll(failures);
                    break;
                }
            }

        }
    }

    public void clearValidation() {
        page.getRoot().forSubTree(x -> {
            if (x instanceof ValidationPresenter) {
                ValidationStatus status = ((ValidationPresenter) x).getValidationStatus();
                status.failures.clear();
                status.isValidated = false;
            }
            ViewComponentBase<?> view = x.getView();
            SubControllerComponent ctrl = view.getController();
            ctrl.validationClassification = ValidationClassification.NOT_VALIDATED;
        });
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
