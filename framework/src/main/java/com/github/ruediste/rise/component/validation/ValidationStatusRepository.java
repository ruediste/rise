package com.github.ruediste.rise.component.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import com.github.ruediste.rise.component.PageScoped;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste1.lambdaPegParser.Var;

/**
 * Repository of the validation status of all {@link Component}s.
 * 
 * <p>
 * The components are referenced weakly. Each for each {@link ValidationFailure}
 * a source is registered, which can be used to remove all validation failures
 * for that source.
 * 
 * <p>
 * In addition, it is stored if a component has been validated.
 */
@PageScoped
public class ValidationStatusRepository {

    private Set<Object> validatedComponents = Collections
            .newSetFromMap(new WeakHashMap<>());

    private Map<Object, Set<ValidationFailure>> componentToFailures = new WeakHashMap<>();

    private Map<Object, Map<Object, List<ValidationFailure>>> sourceComponentToFailures = new WeakHashMap<>();

    public void addFailures(Object source, Object component,
            List<ValidationFailure> failures) {
        sourceComponentToFailures
                .computeIfAbsent(source, x -> new WeakHashMap<>())
                .computeIfAbsent(component, x -> new ArrayList<>())
                .addAll(failures);
        componentToFailures
                .computeIfAbsent(component, x -> new LinkedHashSet<>())
                .addAll(failures);
    }

    public void clearFailures(Object source) {
        Map<Object, List<ValidationFailure>> removedFailures = sourceComponentToFailures
                .remove(source);
        if (removedFailures == null)
            return;
        for (Entry<Object, List<ValidationFailure>> entry : removedFailures
                .entrySet()) {
            Set<ValidationFailure> failuresOfComponent = componentToFailures
                    .get(entry.getKey());
            if (failuresOfComponent != null)
                failuresOfComponent.removeAll(entry.getValue());
        }
    }

    public List<ValidationFailure> getValidationFailures(Object component) {
        ArrayList<ValidationFailure> result = new ArrayList<>();
        forEachNonPresenterInSubTree(component,
                c -> result.addAll(getDirectValidationFailures(c)));
        return result;
    }

    public List<ValidationFailure> getDirectValidationFailures(
            Object component) {
        Set<ValidationFailure> result = componentToFailures.get(component);
        if (result == null)
            return Collections.emptyList();
        return new ArrayList<>(result);
    }

    public boolean isValidated(Object component) {
        Var<Boolean> result = new Var<Boolean>(false);
        forEachNonPresenterInSubTree(component, c -> {
            if (isDirectlyValidated(c))
                result.setValue(true);
        });
        return result.getValue();
    }

    public boolean isDirectlyValidated(Object component) {
        return validatedComponents.contains(component);
    }

    public void setValidated(Object component, boolean isValidated) {
        if (isValidated)
            validatedComponents.add(component);
        else
            validatedComponents.remove(component);
    }

    public ValidationState getDirectValidationState(Object component) {
        if (!isDirectlyValidated(component))
            return ValidationState.NOT_VALIDATED;
        if (getDirectValidationFailures(component).isEmpty())
            return ValidationState.SUCCESS;
        else
            return ValidationState.FAILED;
    }

    public ValidationStatus getValidationStatus(Object component) {
        if (!isValidated(component))
            return new ValidationStatus(ValidationState.NOT_VALIDATED,
                    Collections.emptyList());
        List<ValidationFailure> failures = getValidationFailures(component);
        if (failures.isEmpty())
            return new ValidationStatus(ValidationState.SUCCESS, failures);
        else
            return new ValidationStatus(ValidationState.FAILED, failures);
    }

    public ValidationState getValidationState(Object component) {
        if (!isValidated(component))
            return ValidationState.NOT_VALIDATED;
        if (getValidationFailures(component).isEmpty())
            return ValidationState.SUCCESS;
        else
            return ValidationState.FAILED;
    }

    /**
     * Iterate over all children which are not {@link ValidationStatusPresenter}
     * s.
     */
    public void forEachNonPresenterInSubTree(Object component,
            Consumer<Object> action) {
        action.accept(component);
        if (component instanceof Component)
            ((Component) component).getChildren().forEach(child -> {
                if (child instanceof ValidationStatusPresenter)
                    return;
                else
                    forEachNonPresenterInSubTree(child, action);
            });
    }
}
