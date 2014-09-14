package laf.component.core.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

import laf.component.core.binding.ProxyManger.BindingInformation;
import laf.component.core.binding.ProxyManger.MethodInvocation;
import laf.core.base.attachedProperties.AttachedPropertyBearer;
import net.sf.cglib.proxy.*;

import org.apache.commons.beanutils.PropertyUtils;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public <TView extends AttachedPropertyBearer> void bind(TView view,
			Consumer<TView> expression, boolean oneWay) {
		BindingInformation info = ProxyManger.collectBindingInformation(() -> {
			expression.accept(BindingUtil.<TView> createViewProxy(view
					.getClass()));
		});

		if (info.involvedBindingGroup == null) {
			throw new RuntimeException(
					"No binding group was involved in the expression. "
							+ "Make sure you accsss BindingGroup::proxy() during the execution of the binding expression");
		}

		String viewPath = PropertyUtil.getProperty(info.viewPath);
		String modelPath = PropertyUtil.getProperty(info.modelPath);

		Consumer<Object> pullUp = null;
		Consumer<Object> pushDown = null;

		boolean isModelRead = PropertyUtil.isGetter(info.modelPath
				.get(info.modelPath.size() - 1));

		// set push down lambda if possible
		if ((!oneWay || !isModelRead)
				&& PropertyUtils.isReadable(view, viewPath)
				&& PropertyUtils
						.isWriteable(
								info.involvedBindingGroup.createDummyProxy(),
								modelPath)) {
			pushDown = model -> {
				try {
					Object value = PropertyUtils.getProperty(view, viewPath);
					if (info.transformer != null) {
						if (isModelRead && !info.transformInv) {
							value = ((TwoWayBindingTransformer) info.transformer)
									.transformInv(value);
						} else {
							value = ((BindingTransformer) info.transformer)
									.transform(value);
						}
					}
					PropertyUtils.setProperty(model, modelPath, value);
				} catch (IllegalAccessException | NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getCause());
				}
			};
		}

		// set pull up lambda if possible
		if ((!oneWay || isModelRead)
				&& PropertyUtils
						.isReadable(
								info.involvedBindingGroup.createDummyProxy(),
								modelPath)
				&& PropertyUtils.isWriteable(view, viewPath)) {

			pullUp = model -> {
				try {
					Object value = PropertyUtils.getProperty(model, modelPath);
					if (info.transformer != null) {
						if (isModelRead && !info.transformInv) {
							value = ((BindingTransformer) info.transformer)
									.transform(value);
						} else {
							value = ((TwoWayBindingTransformer) info.transformer)
									.transformInv(value);
						}
					}
					PropertyUtils.setProperty(view, viewPath, value);
				} catch (IllegalAccessException | NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getCause());
				}
			};
		}

		// register binding
		info.involvedBindingGroup.addBindingUntyped(view, pullUp, pushDown);
	}

	@SuppressWarnings("unchecked")
	private static <TView> TView createViewProxy(Class<?> viewClass) {
		Enhancer e = new Enhancer();
		e.setSuperclass(viewClass);
		e.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				BindingInformation info = ProxyManger.getCurrentInformation();
				info.viewPath.add(new MethodInvocation(method, args));

				if (ProxyManger.isTerminal(method.getReturnType())) {
					return Defaults.defaultValue(method.getReturnType());
				}
				return createViewProxy(method.getReturnType());
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
	static public <T> void bind(AttachedPropertyBearer component,
			Supplier<T> bindingAccessor, Consumer<T> pullUp,
			Consumer<T> pushDown) {
		BindingInformation info = ProxyManger.collectBindingInformation(() -> {
			bindingAccessor.get();
		});

		info.involvedBindingGroup
				.addBindingUntyped(component, pullUp, pushDown);
	}
}
