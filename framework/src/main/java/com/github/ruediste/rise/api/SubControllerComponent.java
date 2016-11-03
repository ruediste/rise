package com.github.ruediste.rise.api;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.validation.ValidationClassification;
import com.github.ruediste.rise.component.validation.ValidationPathUtil;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.util.Pair;
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

    public boolean validateView;

    public final Multimap<Pair<Object, String>, ValidationFailure> validationFailureMap = MultimapBuilder.hashKeys()
            .arrayListValues().build();

    public ValidationClassification validationClassification;

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

        public ValidateActionsImpl(Set<? extends ConstraintViolation<?>> violations) {
            super(violations.isEmpty());
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
        for (ConstraintViolation<?> v : violations) {
            String path = ValidationPathUtil.toPathString(v.getPropertyPath());
            validationFailureMap.put(Pair.of(v.getLeafBean(), path), validationUtil.toFailure(v));
        }

    }

    /**
     * Validate this and enable view validation
     */
    protected ValidateActions validate() {
        Set<? extends ConstraintViolation<?>> violations = validator.validate(this);
        this.validateView = true;
        addConstraintViolations(violations);
        validationUtil.performValidation();
        return new ValidateActionsImpl(violations);

    }

    /**
     * Validate this, using the provided validation group and enable view
     * validation
     */
    protected ValidateActions validate(Class<?>... groups) {
        Set<? extends ConstraintViolation<?>> violations = validator.validate(this, groups);
        this.validateView = true;
        addConstraintViolations(violations);
        validationUtil.performValidation();
        return new ValidateActionsImpl(violations);
    }

    public void pushUrl(ActionResult actionResult) {
        componentRequestInfo.pushUrl(util.toUrlSpec(actionResult));
    }

    public void popUrl() {
        componentRequestInfo.popUrl();
    }
}