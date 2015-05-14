package com.github.ruediste.rise.core.aop;

public class InterceptorResultSuccess<T> implements InterceptorResultBefore<T>,
		InterceptorResultOther<T> {

	public final Object result;

	public InterceptorResultSuccess(Object result) {
		this.result = result;
	}

}