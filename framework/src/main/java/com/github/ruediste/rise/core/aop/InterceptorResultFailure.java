package com.github.ruediste.rise.core.aop;

public class InterceptorResultFailure<T> implements InterceptorResultBefore<T>,
		InterceptorResultOther<T> {

	public final Object result;

	public InterceptorResultFailure(Object result) {
		this.result = result;
	}

}