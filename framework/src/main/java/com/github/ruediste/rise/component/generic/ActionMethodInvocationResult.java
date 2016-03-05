package com.github.ruediste.rise.component.generic;

import java.util.function.Function;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Marker interface for action results
 */
public class ActionMethodInvocationResult {

    static class RedirectActionMethodInvocationResult extends ActionMethodInvocationResult {

        ActionResult target;

        public RedirectActionMethodInvocationResult(ActionResult target) {
            this.target = target;
        }
    }

    /**
     * Redirect to the given target
     */
    public static <T extends IController> ActionMethodInvocationResult redirect(Class<T> cls,
            Function<T, com.github.ruediste.rise.core.ActionResult> func) {
        return new RedirectActionMethodInvocationResult(
                func.apply(InjectorsHolder.getInstance(CoreUtil.class).go(cls)));
    }

    static class ShowObjectActionMethodInvocationResult extends ActionMethodInvocationResult {

        Object result;

        public ShowObjectActionMethodInvocationResult(Object result) {
            this.result = result;
        }
    }

    /**
     * Show the properties of the given object as result of the invocation.
     * <p>
     * Note that you can also return the object directly instead
     */
    public static ActionMethodInvocationResult result(Object result) {
        return new ShowObjectActionMethodInvocationResult(result);
    }
}