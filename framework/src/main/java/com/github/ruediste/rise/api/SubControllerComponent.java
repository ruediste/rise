package com.github.ruediste.rise.api;

import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.fragment.ValidationStateBearer;
import com.github.ruediste.rise.component.validation.ValidationException;
import com.github.ruediste.rise.component.validation.ValidationPathUtil;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.util.Var;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Base class for normal controllers and sub controllers
 */
public class SubControllerComponent {

    @Inject
    ComponentUtil util;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    Validator validator;

    @Inject
    ValidationUtil validationUtil;

    public SubControllerComponent() {
        super();
    }

    protected <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    protected <T extends IController> ActionInvocationBuilderKnownController<T> path(Class<T> controllerClass) {
        return util.path(controllerClass);
    }

    protected ActionInvocationBuilder path() {
        return util.path();
    }

    protected void commit() {
        util.commit();
    }

    protected void commit(Runnable inTransaction) {
        util.commit(inTransaction);
    }

    protected void checkAndCommit(Runnable checker) {
        util.checkAndCommit(checker);
    }

    protected void checkAndCommit(Runnable checker, Runnable inTransaction) {
        util.checkAndCommit(checker, inTransaction);
    }

    protected void closePage(HttpRenderResult closePageResult) {
        util.closePage(closePageResult);
    }

    /**
     * In the next render phase the page will be closed and a redirect to the
     * given destination will be returned.
     * 
     * <pre>
     * {@code
     * redirect(go(WelcomeController.class).index());
     * }
     * </pre>
     */
    protected void redirect(ActionResult destination) {
        closePage(new RedirectRenderResult(util.toUrlSpec(destination)));
    }

    private WeakHashMap<Binding, Object> bindings = new WeakHashMap<>();

    void registerBinding(Binding binding) {
        bindings.put(binding, null);
    }

    protected interface SuccessActions<T> {
        /**
         * Return true if the action was successful
         */
        boolean success();

        /**
         * Return true if the action was a failure
         */
        boolean failure();

        /**
         * When the action was successful, run the provided runnable
         */
        T onSuccess(Runnable r);

        /**
         * When the action was a failure, run the provided runnable
         */
        T onFailure(Runnable r);

    }

    /**
     * Pull the data of all bindings up from the model to the view
     */
    protected void pullUp() {
        getValidationStateBearers().forEach(b -> {
            b.clearDirectValidationFailures();
            b.setValidated(false);
        });
        getBindings().forEach(b -> b.pullUp());
    }

    private Stream<ValidationStateBearer> getValidationStateBearers() {
        return getBindings().map(b -> b.getValidationStateBearer()).filter(x -> x != null);
    }

    protected interface ValidateActions extends SuccessActions<ValidateActions> {

        Set<? extends ConstraintViolation<?>> violations();

    }

    private static abstract class SuccessActionsImpl<T> implements SuccessActions<T> {

        private boolean success;

        public SuccessActionsImpl(boolean success) {
            this.success = success;
        }

        abstract protected T t();

        @Override
        public boolean success() {
            return success;
        }

        @Override
        public boolean failure() {
            return !success;
        }

        @Override
        public T onSuccess(Runnable r) {
            if (success)
                r.run();
            return t();
        }

        @Override
        public T onFailure(Runnable r) {
            if (!success)
                r.run();
            return t();
        }

    }

    private static class ValidateActionsImpl extends SuccessActionsImpl<ValidateActions>implements ValidateActions {

        private Set<? extends ConstraintViolation<?>> violations;

        public ValidateActionsImpl(boolean success, Set<? extends ConstraintViolation<?>> violations) {
            super(success && violations.isEmpty());
            this.violations = violations;
        }

        @Override
        protected ValidateActions t() {
            return this;
        }

        @Override
        public Set<? extends ConstraintViolation<?>> violations() {
            return violations;
        }
    }

    /**
     * Set constraint violations for this controller. Defers applying until the
     * components have been constructed during initial page requests.
     */
    protected void addConstraintViolations(Set<? extends ConstraintViolation<?>> violations) {

        if (componentRequestInfo.isInitialRequest())
            componentRequestInfo.addInitialContraintViolations(this, violations);
        else
            applyConstraintViolations(violations);

    }

    /**
     * Validate the value of this group and update the validation state of the
     * components
     */
    protected ValidateActions validate() {
        Set<? extends ConstraintViolation<?>> violations = validator.validate(this);
        clearValidationFailures();
        addConstraintViolations(violations);
        return new ValidateActionsImpl(true, violations);

    }

    /**
     * Validate the value of this group and update the validation state of the
     * components
     */
    protected ValidateActions validate(Class<?>... groups) {
        Set<? extends ConstraintViolation<?>> violations = validator.validate(this, groups);
        clearValidationFailures();
        addConstraintViolations(violations);
        return new ValidateActionsImpl(true, violations);
    }

    protected interface PushDownActions extends SuccessActions<PushDownActions> {

        /**
         * Validate the value of this group, both if the push down was
         * successful or failed. The success state value represents both the
         * push down and the validation.
         */
        ValidateActions validate();

        /**
         * Validate the value of this group, both if the push down was
         * successful or failed. The success state value represents both the
         * push down and the validation.
         */
        ValidateActions validate(Class<?>... groups);
    }

    private class PushDownActionsImpl extends SuccessActionsImpl<PushDownActions>implements PushDownActions {

        public PushDownActionsImpl(boolean success) {
            super(success);
        }

        @Override
        public ValidateActions validate() {
            Set<? extends ConstraintViolation<?>> violations = validator.validate(this);
            addConstraintViolations(violations);
            return new ValidateActionsImpl(success(), violations);
        }

        @Override
        public ValidateActions validate(Class<?>... groups) {
            Set<? extends ConstraintViolation<?>> violations = validator.validate(this, groups);
            addConstraintViolations(violations);
            return new ValidateActionsImpl(success(), violations);
        }

        @Override
        protected PushDownActions t() {
            return this;
        }

    }

    /**
     * Push down but do not show errors in the components
     * 
     * @return true if the push down was successful, false otherwise
     */
    protected boolean silentPushDown() {
        Var<Boolean> success = new Var<Boolean>(true);
        getBindings().forEach(b -> {
            try {
                b.pushDown();
            } catch (ValidationException e) {
                success.setValue(false);
            }
        });
        return success.getValue();
    }

    /**
     * Push down and show validation errors during push down in the components.
     * The returned value can be used to determine if the attempt was successful
     * or if there were errors.
     */
    protected PushDownActions tryPushDown() {
        clearValidationFailures();

        Var<Boolean> success = new Var<Boolean>(true);
        getBindings().forEach(b -> {
            try {
                b.pushDown();
            } catch (ValidationException e) {
                success.setValue(false);
                b.getValidationStateBearer().addFailures(e.getFailures());
            }
            b.getValidationStateBearer().setValidated(true);
        });
        return new PushDownActionsImpl(success.getValue());
    }

    private void clearValidationFailures() {
        getValidationStateBearers().forEach(b -> b.clearDirectValidationFailures());
    }

    /**
     * Push the data of all bindings down from the view to the model
     */
    protected void pushDown() {
        getBindings().forEach(Binding::pushDown);
    }

    private Stream<Binding> getBindings() {
        return bindings.keySet().stream();
    }

    /**
     * Directly apply violations to a binding group. Do not use this method
     * during an initial page request. Usually, it is better to use
     * {@link #addConstraintViolations(Set)}.
     */
    public void applyConstraintViolations(Set<? extends ConstraintViolation<?>> violations) {

        Multimap<String, ConstraintViolation<?>> violationMap = MultimapBuilder.hashKeys().arrayListValues().build();

        for (ConstraintViolation<?> v : violations) {
            violationMap.put(ValidationPathUtil.toPathString(v.getPropertyPath()), v);
        }

        getBindings().filter(x -> x.getValidationStateBearer() != null).forEach(b -> {
            ValidationStateBearer bearer = b.getValidationStateBearer();
            bearer.addFailures(validationUtil.toFailures(violationMap.get(b.getModelPath())));
            bearer.setValidated(true);
        });
    }
}