package com.github.ruediste.rise.api;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.i18n.ValidationFailure;
import com.github.ruediste.rise.core.i18n.ValidationFailureImpl;
import com.github.ruediste.rise.core.i18n.ValidationFailureSeverity;
import com.github.ruediste.rise.core.i18n.ValidationUtil;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste1.i18n.lString.LString;
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
    ValidationUtil validationUtil;

    public Multimap<Pair<Object, String>, ValidationFailure> validationFailureMap = MultimapBuilder.hashKeys()
            .arrayListValues().build();

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

    protected static class SuccessActions {

        private boolean success;

        public SuccessActions(boolean success) {
            this.success = success;
        }

        /**
         * Return true if the action was successful
         */
        public boolean success() {
            return success;
        }

        /**
         * Return true if the action was a failure
         */
        public boolean failure() {
            return !success;
        }

        /**
         * When the action was successful, run the provided runnable
         */
        public SuccessActions onSuccess(Runnable r) {
            if (success)
                r.run();
            return this;
        }

        /**
         * When the action was a failure, run the provided runnable
         */
        public SuccessActions onFailure(Runnable r) {
            if (!success)
                r.run();
            return this;
        }

    }

    public interface ValidationFailureCollector {
        default void addFailure(Object bean, String property, LString message, ValidationFailureSeverity severity) {
            addFailure(bean, property, new ValidationFailureImpl(message, severity));
        }

        default void addError(Object bean, String property, LString message) {
            addFailure(bean, property, message, ValidationFailureSeverity.ERROR);
        }

        default void addWarning(Object bean, String property, LString message) {
            addFailure(bean, property, message, ValidationFailureSeverity.WARNING);
        }

        default void addInfo(Object bean, String property, LString message) {
            addFailure(bean, property, message, ValidationFailureSeverity.INFO);
        }

        void addFailure(Object bean, String property, ValidationFailure failure);

        /**
         * Set constraint violations for this controller. Defers applying until
         * the components have been constructed during initial page requests.
         */
        void addConstraintViolations(Set<? extends ConstraintViolation<?>> violations);

        void validate(Object target, Class<?>... groups);

    }

    /**
     * Called to collect the validation failures of a controller.
     */
    public void performValidation(ValidationFailureCollector collector) {

    }

    /**
     * Validate this and enable view validation
     */
    protected SuccessActions validate() {
        return new SuccessActions(validationUtil.validate(this));

    }

    public void pushUrl(ActionResult actionResult) {
        componentRequestInfo.pushUrl(util.toUrlSpec(actionResult));
    }

    public void popUrl() {
        componentRequestInfo.popUrl();
    }
}