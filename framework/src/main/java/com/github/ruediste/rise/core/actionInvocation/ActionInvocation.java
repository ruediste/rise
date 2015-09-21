package com.github.ruediste.rise.core.actionInvocation;

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.util.MethodInvocation;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

/**
 * An action path represents an invocation of an action method. It is composed
 * of {@link ActionInvocation} elements, which describe the path over possibly
 * multiple embedded controllers to the final action.
 *
 * <p>
 * {@link AttachedProperty AttachedProperties} can be attached to an action
 * path, allowing other modules to transport information via action paths.
 * However, they are not sent along with an action path to the client
 * </p>
 *
 * <p>
 * Using {@link ActionInvocationParameter}, arbitrary strings can be attached to
 * {@link ActionInvocation}s.
 * </p>
 *
 */
public class ActionInvocation<T> extends AttachedPropertyBearerBase {

    /**
     * Parameters which will be sent to the client and back again
     */
    final Map<String, String[]> parameters = new HashMap<>();
    public MethodInvocation<T> methodInvocation;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(methodInvocation)
                .toString();
    }

    /**
     * Determine if this and the other path represent calls to the same action
     * method. The parameters values are ignored.
     */
    public boolean isCallToSameActionMethod(ActionInvocation<?> other) {
        return isCallToSameActionMethod(other,
                new MethodInvocation.ParameterValueEquality<Object, Object>() {

                    @Override
                    public boolean equals(Object a, Object b) {
                        return true;
                    }
                });
    }

    /**
     * Determine if this and the other path represent calls to the same action
     * method. The Parameters are compared by the provided
     */
    public <O> boolean isCallToSameActionMethod(ActionInvocation<O> other,
            MethodInvocation.ParameterValueEquality<? super T, ? super O> comparator) {

        return methodInvocation.isCallToSameMethod(other.methodInvocation,
                comparator);
    }

    /**
     * Create a new {@link ActionInvocation} with the arguments mapped by the
     * given function. The associated {@link ActionInvocationParameter}s are
     * copied
     */
    public <P> ActionInvocation<P> map(final Function<? super T, P> func) {
        return mapWithType((a, b) -> func.apply(b));
    }

    public <P> ActionInvocation<P> mapWithType(
            BiFunction<AnnotatedType, ? super T, P> func) {
        ActionInvocation<P> result = new ActionInvocation<>();
        result.getParameters().putAll(getParameters());
        result.getAttachedPropertyMap().putAll(this);
        result.methodInvocation = methodInvocation.map(func);
        return result;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }
}
