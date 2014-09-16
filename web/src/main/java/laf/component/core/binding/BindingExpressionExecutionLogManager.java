package laf.component.core.binding;

import java.lang.reflect.Method;
import java.util.Date;

/*
 * @startuml doc-files/bindingInformationCollector.png
 *
 * class BindingUtil {
 * 	createViewProxy()
 * }
 *
 * BindingUtil ..> ViewProxy
 *
 * class ViewProxy{
 * }
 *
 * note left of ViewProxy
 * record invoked methos
 * in current BindingExpressionExecutionLog
 * end note
 *
 * class BindingGroup {
 *   createModelProxy()
 * }
 *
 * BindingGroup ..> ModelProxy
 * class ModelProxy {
 * }
 *
 * note right of ModelProxy
 * record invoked methos
 * in current BindingExpressionExecutionLog
 * end note
 *
 * class BindingExpressionExecutionLogManager {
 *   getCurrentBindingInformation()
 *   collectBindingInformation(Runnable)
 * }
 *
 * class BindingExpressionExecutionLog {
 *   involvedBindingGroup
 *   modelPath
 *   viewPath
 *   transformer
 * }
 *
 * BindingUtil ..> BindingExpressionExecutionLogManager
 * BindingGroup ..> BindingExpressionExecutionLogManager
 * BindingExpressionExecutionLogManager --> BindingExpressionExecutionLog
 * @enduml
 */

/**
 * Helper class to record binding information while executing a binding
 * expression.
 *
 * <p>
 * <img src="doc-files/bindingInformationCollector.png" />
 * </p>
 *
 * <p>
 * {@link BindingUtil} and {@link BindingGroup} create proxies which record
 * method invocations in the {@link BindingExpressionExecutionLog}. The recorded
 * information is used to determine the bindings to be created.
 * </p>
 */
class BindingExpressionExecutionLogManager {

	static class MethodInvocation {
		Method method;
		Object[] args;

		public MethodInvocation(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}
	}

	private static ThreadLocal<BindingExpressionExecutionLog> currentLog = new ThreadLocal<BindingExpressionExecutionLog>();

	static BindingExpressionExecutionLog getCurrentLog() {
		return currentLog.get();
	}

	/**
	 * Log the execution of a binding expression. The provided runnable should
	 * invoke the binding expression, supplying proxies which record invoked
	 * methods in the current log. The log is then returned.
	 */
	static BindingExpressionExecutionLog collectBindingExpressionLog(
			Runnable runnable) {
		if (currentLog.get() != null) {
			throw new RuntimeException(
					"Attempt to collect binding information while collecting binding information is already in progress");
		}

		BindingExpressionExecutionLog result = new BindingExpressionExecutionLog();
		try {
			currentLog.set(result);
			runnable.run();
		} finally {
			currentLog.set(null);
		}
		return result;
	}

	/**
	 * Utility method to determine if the return type of a method should be
	 * considered terminal or if a proxy should be returned.
	 */
	static boolean isTerminal(Class<?> clazz) {
		return clazz.isPrimitive() || String.class.equals(clazz)
				|| Date.class.equals(clazz);
	}
}
