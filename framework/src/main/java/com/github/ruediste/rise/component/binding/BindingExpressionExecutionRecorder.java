package com.github.ruediste.rise.component.binding;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Helper class to record binding information while executing a binding
 * expression.
 *
 * <p>
 * {@link BindingUtil} and {@link BindingGroup} create proxies which record
 * method invocations in the {@link BindingExpressionExecutionRecord}. The
 * recorded information is used to determine the bindings to be created.
 * </p>
 * 
 * <img src="doc-files/bindingPropertyAccessRecording.png" alt="">
 */
public class BindingExpressionExecutionRecorder {

    static class MethodInvocation {
        Method method;
        Object[] args;

        public MethodInvocation(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }
    }

    private static ThreadLocal<BindingExpressionExecutionRecord> currentLog = new ThreadLocal<BindingExpressionExecutionRecord>();

    static BindingExpressionExecutionRecord getCurrentLog() {
        return currentLog.get();
    }

    /**
     * Log the execution of a binding expression. The provided runnable should
     * invoke the binding expression, supplying proxies which record invoked
     * methods in the current log. The log is then returned.
     */
    static BindingExpressionExecutionRecord collectBindingExpressionLog(Runnable runnable) {
        BindingExpressionExecutionRecord oldLog = currentLog.get();
        BindingExpressionExecutionRecord result = new BindingExpressionExecutionRecord();
        try {
            currentLog.set(result);
            runnable.run();
        } finally {
            currentLog.set(oldLog);
        }
        return result;
    }

    /**
     * Utility method to determine if the return type of a method should be
     * considered terminal or if a proxy should be returned.
     */
    static boolean isTerminal(Class<?> clazz) {
        return clazz.isPrimitive() || String.class.equals(clazz) || Date.class.equals(clazz);
    }
}
