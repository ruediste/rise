package laf.component.core.binding;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Helper class to implement the binding mechanism. Used to access and manage
 * any proxy objects involved.
 */
class ProxyManger {

	static class MethodInvocation {
		Method method;
		Object[] args;

		public MethodInvocation(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}
	}

	static class BindingInformation {
		BindingGroup<?> involvedBindingGroup;
		List<MethodInvocation> modelPath = new ArrayList<>();
		List<MethodInvocation> viewPath = new ArrayList<>();
		BindingTransformer<?, ?> transformer;
		boolean transformInv;
	}

	private static ThreadLocal<BindingInformation> currentBindingInformation = new ThreadLocal<ProxyManger.BindingInformation>();

	static BindingInformation getCurrentInformation() {
		return currentBindingInformation.get();
	}

	/**
	 * Collect information about a binding
	 */
	static BindingInformation collectBindingInformation(Runnable runnable) {
		if (currentBindingInformation.get() != null) {
			throw new RuntimeException(
					"Attempt to collect binding information while collecting binding information is already in progress");
		}

		BindingInformation result = new BindingInformation();
		try {
			currentBindingInformation.set(result);
			runnable.run();
		} finally {
			currentBindingInformation.set(null);
		}
		return result;
	}

	static boolean isTerminal(Class<?> clazz) {
		return clazz.isPrimitive() || String.class.equals(clazz)
				|| Date.class.equals(clazz);
	}
}
