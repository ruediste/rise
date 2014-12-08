package laf.component.core.beanutil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.function.Consumer;

import javax.inject.Inject;

import net.sf.cglib.proxy.*;

import org.apache.commons.beanutils.BeanUtilsBean;

import com.google.common.base.Defaults;
import com.google.common.reflect.TypeToken;

public class BeanUtil {

	@Inject
	BeanUtilsBean beanUtils;

	private final class ProxyCallback implements MethodInterceptor {
		BeanProperty property;
		TypeToken<?> currentProxyType;

		public ProxyCallback(BeanProperty property, TypeToken<?> lastType) {
			super();
			this.property = property;
			currentProxyType = lastType;
		}

		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy proxy) throws Throwable {
			for (PropertyDescriptor desc : beanUtils.getPropertyUtils()
					.getPropertyDescriptors(currentProxyType)) {
			}

			property = property.with(null);
			if (isTerminal(method.getReturnType())) {
				// return a default value as return type
				currentProxyType = null;
				return Defaults.defaultValue(method.getReturnType());
			} else {
				// update this
				currentProxyType = currentProxyType.resolveType(method
						.getGenericReturnType());

				// return a proxy of the return type, using this as callback
				Enhancer e = new Enhancer();
				e.setSuperclass(currentProxyType.getRawType());
				e.setCallback(this);
				return e.create();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T createPropertyExctractionProxy(TypeToken<T> startType) {
		Enhancer e = new Enhancer();
		e.setSuperclass(startType.getRawType());
		e.setCallback(new ProxyCallback(BeanProperty.of(startType.getType()),
				startType));
		return (T) e.create();
	}

	public BeanProperty getProperty(Object proxy) {
		ProxyCallback callback = (ProxyCallback) ((Factory) proxy)
				.getCallback(0);
		return callback.property;
	}

	public <T> BeanProperty extractProperty(TypeToken<T> startType,
			Consumer<T> propertyAccessor) {
		T proxy = createPropertyExctractionProxy(startType);
		propertyAccessor.accept(proxy);
		return getProperty(proxy);
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