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
import com.github.ruediste.rise.component.validation.ValidationPathUtil;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.rise.util.Var;
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
 * Requirements:
 * <ul>
 * <li>Validation is triggered explicitly by the controller. This keeps page
 * reloads transparent.</li>
 * <li>During a reload request, a controller has to be able to request
 * validation and immediately (without a further page reload) react to it. It is
 * sufficient if the last version of the view rendered is validated.</li>
 * <li>The {@link ValidationPresenter} for each {@link ValidationFailure} needs
 * to be redetermined during each render process since the structure of the page
 * might have been changed</li>
 * <li>Hidden validation presenters cannot present failures</li>
 * <li>A subcontroller is always validated if it's parent controller is
 * validated.</li>
 * <li>The controller-subcontroller relationship is determined by the view</li>
 * <li>Both controllers and components can contribute validation failures</li>
 * </ul>
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
     * Validate the components of the view of the given controller, including
     * subviews/subcontrollers and return true if no failures have been found.
     * The validation failures of the components and controllers are updated.
     */
    public boolean validate(SubControllerComponent rootCtrl) {
        Component<?> rootComponent = findRootComponent(rootCtrl);
        Var<Boolean> success = Var.of(true);

        // validate components in subtree and collect controllers
        HashSet<SubControllerComponent> controllers = new HashSet<>();
        rootComponent.forSubTree(c -> {
            c.isValidated = true;
            c.validationFailures = c.validate();
            if (!c.validationFailures.isEmpty())
                success.set(false);
            controllers.add(c.getView().getController());
            if (c instanceof ValidationPresenter)
                ((ValidationPresenter) c).getValidationStatus().isValidated = true;
        });

        // validate controllers
        controllers.forEach(ctrl -> {
            Collector collector = new Collector();
            ctrl.performValidation(collector);
            ctrl.validationFailureMap = collector.validationFailureMap;
            if (!ctrl.validationFailureMap.isEmpty())
                success.set(false);
        });
        return success.get();
    }

    public void clearValidation(SubControllerComponent rootCtrl) {
        Component<?> rootComponent = findRootComponent(rootCtrl);
        HashSet<SubControllerComponent> controllers = new HashSet<>();
        rootComponent.forSubTree(c -> {
            c.isValidated = false;
            c.validationFailures = Collections.emptyList();
            controllers.add(c.getView().getController());
            if (c instanceof ValidationPresenter)
                ((ValidationPresenter) c).getValidationStatus().isValidated = false;
        });

        controllers.forEach(ctrl -> {
            ctrl.validationFailureMap.clear();
        });
    }

    private Component<?> findRootComponent(SubControllerComponent rootCtrl) {
        // find view of the controller
        Var<ViewComponentBase<?>> rootView = Var.of(null);
        page.getRoot().forSubTreePartial(c -> {
            if (c.getView().getController() == rootCtrl) {
                rootView.set(c.getView());
                return false;
            }
            return true;
        });

        if (rootView.get() == null)
            throw new RuntimeException("View for " + rootCtrl + " not found");

        // get root component
        Component<?> rootComponent = rootView.get().parentComponent;
        if (rootComponent == null)
            rootComponent = page.getRoot();
        return rootComponent;
    }

    /**
     * Update the status of all validation presenters
     */
    public void updateValidationPresenters() {
        Component<?> pageRoot = page.getRoot();

        // build helper data structures for easy access to required objects
        List<Component<?>> components = new ArrayList<>();
        List<ValidationPresenter> validationPresenters = new ArrayList<>();
        Set<ViewComponentBase<?>> views = new HashSet<>();
        Set<SubControllerComponent> controllers = new HashSet<>();
        pageRoot.forSubTreePartial(component -> {
            components.add(component);
            if (component instanceof ValidationPresenter) {
                if (!((ValidationPresenter) component).getValidationStatus().isOutputSuspended)
                    validationPresenters.add((ValidationPresenter) component);
            }
            views.add(component.getView());
            controllers.add(component.getView().getController());
            return true;
        });

        Map<SubControllerComponent, List<SubControllerComponent>> controllerAncestors = new HashMap<>();
        fillControllerAncestors(controllerAncestors, page.getRoot(), Collections.emptyList());

        // clear validationPresenters
        validationPresenters.forEach(x -> {
            ValidationStatus status = x.getValidationStatus();
            status.failures.clear();
        });
        page.getUnhandledValidationFailures().clear();

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

            // add component failures
            failures.addAll(component.validationFailures);

            addFailuresToNearestPresenter(component, failures);
        }

        // pull remaining failures from controllers into view
        for (ViewComponentBase<?> view : views) {
            SubControllerComponent controller = view.getController();
            if (!controller.validationFailureMap.isEmpty()) {
                List<ValidationFailure> unhandledFailures = controller.validationFailureMap.values().stream()
                        .filter(x -> !handledControllerFailures.contains(x)).collect(toList());
                if (!unhandledFailures.isEmpty()) {
                    if (view.parentComponent != null)
                        addFailuresToNearestPresenter(view.parentComponent, unhandledFailures);
                    else
                        page.getUnhandledValidationFailures().addAll(unhandledFailures);
                }
            }
        }

    }

    private void addFailuresToNearestPresenter(Component<?> component, List<ValidationFailure> failures) {
        if (!failures.isEmpty()) {
            // add failures to presenter
            for (Component<?> ancestor : component.parents()) {
                if (ancestor instanceof ValidationPresenter) {
                    ValidationStatus status = ((ValidationPresenter) ancestor).getValidationStatus();
                    if (!status.isOutputSuspended) {
                        status.failures.addAll(failures);
                        return;
                    }
                }
            }

            page.getUnhandledValidationFailures().addAll(failures);
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
            ctrl.validationFailureMap.clear();
        });
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
