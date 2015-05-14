package com.github.ruediste.rise.core.aop;

public class InterceptorResultProceed<T> implements InterceptorResultBefore<T> {

	public final T arg;

	public InterceptorResultProceed(T arg) {
		this.arg = arg;
	}

}