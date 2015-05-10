package com.github.ruediste.rise.component.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.component.binding.BindingExpressionExecutionLogManager.MethodInvocation;
import com.google.common.base.Defaults;

/**
 * Interface to define bindings.
 *
 * @see BindingGroup
 */
public class BindingUtil {

	/**
	 * Establish a one-way or two-way binding, depending on the properties
	 * involved
	 */
	static public <TView extends AttachedPropertyBearer> void bind(TView view,
			Consumer<TView> expression) {
		bind(view, expression, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static public <TComponent extends AttachedPropertyBearer> void bind(
			TComponent component, Consumer<TComponent> expression,
			boolean oneWay) {
		BindingExpressionExecutionLog info = BindingExpressionExecutionLogManager
				.collectBindingExpressionLog(() -> {
					expression.accept(BindingUtil
							.<TComponent> createComponentProxy(component
									.getClass()));
				});

		if (info.involvedBindingGroup == null) {
			throw new RuntimeException(
					"No binding group was involved in the expression. "
							+ "Make sure you accsss BindingGroup::proxy() during the execution of the binding expression");
		}

		Binding<?> binding = new Binding<>();
		binding.setComponent(component);
		binding.setComponentProperty(BeanutilPropertyGenerationUtil
				.getProperty(info.componentPath));
		binding.setModelProperty(BeanutilPropertyGenerationUtil
				.getProperty(info.modelPath));

		boolean isModelRead = BeanutilPropertyGenerationUtil
				.isGetter(info.modelPath.get(info.modelPath.size() - 1));

		boolean doPullUp;
		boolean doPushDown;

		// determine binding direction
		if (oneWay) {
			doPullUp = isModelRead;
			doPushDown = !doPullUp;
		} else {
			doPushDown = PropertyUtils.isReadable(component,
					binding.getComponentProperty())
					&& PropertyUtils.isWriteable(
							info.involvedBindingGroup.createDummyProxy(),
							binding.getModelProperty());
			doPullUp = PropertyUtils.isWriteable(component,
					binding.getComponentProperty())
					&& PropertyUtils.isReadable(
							info.involvedBindingGroup.createDummyProxy(),
							binding.getModelProperty());
		}
		// set push down lambda if possible
		if (doPushDown) {

			binding.setPushDown(model -> {
				try {
					Object value = PropertyUtils.getProperty(component,
							binding.getComponentProperty());
					if (info.transformer != null) {
						if (isModelRead && !info.transformInv) {
							value = ((TwoWayBindingTransformer) info.transformer)
									.transformInv(value);
						} else {
							value = ((BindingTransformer) info.transformer)
									.transform(value);
						}
					}
					PropertyUtils.setProperty(model,
							binding.getModelProperty(), value);
				} catch (IllegalAccessException | NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getCause());
				}
			});
		}

		// set pull up lambda if possible
		if (doPullUp) {

			binding.setPullUp(model -> {
				try {
					Object value = PropertyUtils.getProperty(model,
							binding.getModelProperty());
					if (info.transformer != null) {
						if (isModelRead && !info.transformInv) {
							value = ((BindingTransformer) info.transformer)
									.transform(value);
						} else {
							value = ((TwoWayBindingTransformer) info.transformer)
									.transformInv(value);
						}
					}
					PropertyUtils.setProperty(component,
							binding.getComponentProperty(), value);
				} catch (IllegalAccessException | NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getCause());
				}
			});
		}

		// register binding
		info.involvedBindingGroup.addBindingUntyped(binding);
	}

	@SuppressWarnings("unchecked")
	private static <TView> TView createComponentProxy(Class<?> viewClass) {
		Enhancer e = new Enhancer();
		e.setSuperclass(viewClass);
		e.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				BindingExpressionExecutionLog info = BindingExpressionExecutionLogManager
						.getCurrentLog();
				info.componentPath.add(new MethodInvocation(method, args));

				if (BindingExpressionExecutionLogManager.isTerminal(method
						.getReturnType())) {
					return Defaults.defaultValue(method.getReturnType());
				}
				return createComponentProxy(method.getReturnType());
			}
		});

		return (TView) e.create();
	}

	/**
	 * Establish a one-way binding accoding to the given expression
	 */
	static public <TView extends AttachedPropertyBearer> void bindOneWay(
			TView view, Consumer<TView> expression) {
		bind(view, expression, true);
	}

	/**
	 * Establish a binding for a given binding, using the two provided lambda
	 * expressions.
	 * 
	 * @param bindingAccessor
	 *            supplier of the proxy of a binding group (
	 *            {@link BindingGroup#proxy()})
	 * @param pullUp
	 *            lambda expression to pull up
	 * @param pushDown
	 *            lambda expression to push down
	 */
	public static <T> void bind(Supplier<T> bindingAccessor, Binding<T> binding) {
		BindingExpressionExecutionLog info = BindingExpressionExecutionLogManager
				.collectBindingExpressionLog(() -> {
					bindingAccessor.get();
				});

		info.involvedBindingGroup.addBindingUntyped(binding);
	}
}
