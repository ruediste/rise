package com.github.ruediste.rise.core.aop;

/**
 * Interceptor for AOP programming
 * 
 * @see AopUtil
 */
public abstract class Interceptor<T> {

	protected InterceptorResultProceed<T> proceed(T arg) {
		return new InterceptorResultProceed<>(arg);
	}

	protected InterceptorResultSuccess<T> success(Object result) {
		return new InterceptorResultSuccess<T>(result);
	}

	protected InterceptorResultFailure<T> failure(Object result) {
		return new InterceptorResultFailure<T>(result);
	}

	protected InterceptorResultRestart<T> restart() {
		return new InterceptorResultRestart<>();
	}

	/**
	 * Called before the method is invoked.
	 */
	public abstract InterceptorResultBefore<T> onBefore(
			InterceptedInvocation invocation);

	/**
	 * called after the method has been executed successfully
	 */
	public abstract InterceptorResultOther<T> onSuccess(T arg,
			InterceptedInvocation invocation, Object result);

	/**
	 * called after the method has thrown an exception
	 */
	public abstract InterceptorResultOther<T> onFailure(T arg,
			InterceptedInvocation invocation, Throwable t);

	/**
	 * called after the method has been invoked, sucessfully or with a failure
	 * (like java's finally)
	 */
	public abstract InterceptorResultOther<T> onComplete(T arg,
			InterceptedInvocation invocation);
}
