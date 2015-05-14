package com.github.ruediste.rise.core.aop;

import java.lang.reflect.Method;

public interface InterceptedInvocation {
	Method getMethod();

	Object getInstance();

	Object[] getArgs();
}