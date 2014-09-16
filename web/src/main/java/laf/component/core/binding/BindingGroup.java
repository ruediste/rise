package laf.component.core.binding;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import laf.component.core.binding.BindingExpressionExecutionLogManager.MethodInvocation;
import laf.component.core.tree.ComponentBase;
import laf.core.base.attachedProperties.AttachedProperty;
import laf.core.base.attachedProperties.AttachedPropertyBearer;
import net.sf.cglib.proxy.*;

import com.google.common.base.Defaults;
import com.google.common.reflect.TypeToken;

/*
 * @startuml doc-files/overview.png
 *
 * class View {
 * }
 *
 * class Controller{
 * }
 * class Component{
 * }
 *
 * class BindingGroup{
 * }
 *
 * class BindingEntry{
 * }
 *
 * class Entity {
 * }
 * View -right-> Controller
 * View -right-> Component
 * BindingGroup -up-> BindingEntry
 * Controller -down-> BindingGroup
 * Controller -down-> Entity
 * BindingEntry -down-> Entity
 * BindingEntry -up-> Component
 *
 * @enduml
 */
/**
 * A group of bindings managed by a controller.
 *
 * <p>
 * Data binding is used to move data between the domain objects and the view
 * components. Properties are bound together in a typesafe manner using lambda
 * expressions.
 * </p>
 *
 * <p>
 * <img src="doc-files/overview.png" />
 * </p>
 *
 * <p>
 * The binding is triggered explicitly. The controller instantiates a
 * {@link BindingGroup} and references it in an instance variable. If the data
 * is ready, the controller uses the {@link #pullUp(Object)} method to fill the
 * components with data. To retrieve the data from the components, it uses
 * {@link #pushDown(Object)}. To the view the binding group is exposed via a
 * function returning {@link #proxy()}.
 * </p>
 *
 * <p>
 * The component classes have a
 * {@link ComponentBase#bind(java.util.function.Consumer)} method which accepts
 * a lambda expression. The lambda expression has the component as parameter and
 * sets a property of the component to some value retrieved via a binding group
 * exposed by the controller.
 * </p>
 *
 * <p>
 * While establishing the binding, the lambda expression is called with a
 * dynamic proxy of the component class as parameter. During the invocation,
 * {@link BindingGroup#proxy()} is configured to return a dynamic proxy, too.
 * The two dynamic proxies record the methods invoked on themselves. This
 * information is used to determine which properties are accessed.
 * </p>
 *
 * <p>
 * When the involved properties are known, it is determined if the property read
 * by the lambda expression is writeable and if the property set by the lambda
 * expression is readable. If both conditions are true, the binding is two-way,
 * otherwise it is one-way, in the same direction as specified by the lambda
 * expression.
 * </p>
 *
 * <p>
 * When the binding is established, the lambda expression is not called again.
 * The properties are read and written using reflection. Thus any processing
 * logic contained in the expression does not affect the binding.
 * </p>
 *
 * <p>
 * <strong> One Way Bindings </strong><br/>
 * Using {@link ComponentBase#bindOneWay(java.util.function.Consumer)} the check
 * if a binding could be two way is suppressed and the binding always takes the
 * direction as specified by the lambda expression.
 * </p>
 *
 * <p>
 * <strong> Explicit Bindings </strong><br/>
 * Using
 * {@link ComponentBase#bind(java.util.function.Consumer, java.util.function.Consumer)}
 * an explicit binding is established. The two lambda expressions are always
 * invoked with the real objects. This can be used to easily define
 * one-of-a-kind transformations in place. Either expression can be null, in
 * which case it is ignored.
 * </p>
 *
 * <p>
 * <strong> Transformers </strong><br/>
 * Transformers allow a value to be transformed. When a lambda expression is
 * executed to establish a binding, the usage of any transformer is recorded and
 * integrated in the binding. Transformers can be one-way (implementing only
 * {@link BindingTransformer}) or two-way (implementing
 * {@link TwoWayBindingTransformer}). If it is attempted to establish a two-way
 * binding using an one-way transformer, an exception is raised.
 * </p>
 */
public class BindingGroup<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Class<?> tClass;

	private final AttachedProperty<AttachedPropertyBearer, Set<Binding<T>>> bindings = new AttachedProperty<>();

	WeakHashMap<AttachedPropertyBearer, Object> components = new WeakHashMap<>();

	T data;

	private interface Binding<T> {
		void pullUp();

		void pushDown();
	}

	public BindingGroup(Class<T> cls) {
		tClass = cls;
	}

	public BindingGroup(TypeToken<T> token) {
		tClass = token.getRawType();
	}

	private Stream<Binding<T>> getBindings() {
		return components.keySet().stream()
				.flatMap(c -> bindings.get(c).stream());
	}

	public void pullUp() {
		getBindings().forEach(b -> b.pullUp());
	}

	public void pushDown() {
		getBindings().forEach(b -> b.pushDown());
	}

	public T get() {
		return data;
	}

	public void set(T data) {
		this.data = data;
	}

	public T proxy() {
		BindingExpressionExecutionLog info = BindingExpressionExecutionLogManager.getCurrentLog();
		info.involvedBindingGroup = this;
		return createModelProxy(tClass);
	}

	@SuppressWarnings("unchecked")
	private <TModel> TModel createModelProxy(Class<?> modelClass) {
		Enhancer e = new Enhancer();
		e.setSuperclass(modelClass);
		e.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				BindingExpressionExecutionLog info = BindingExpressionExecutionLogManager.getCurrentLog();
				info.modelPath.add(new MethodInvocation(method, args));

				Class<?> returnType = method.getReturnType();
				if (isTerminal(returnType)) {
					return Defaults.defaultValue(returnType);
				}
				return createModelProxy(returnType);
			}

		});

		return (TModel) e.create();
	}

	private boolean isTerminal(Class<?> clazz) {
		return clazz.isPrimitive() || String.class.equals(clazz)
				|| Date.class.equals(clazz);
	}

	/**
	 * Create a dummy usable for use with beanutils
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	T createDummyProxy() {
		return (T) createDummyProxy(tClass);
	}

	private Object createDummyProxy(Class<?> cls) {
		Enhancer e = new Enhancer();
		e.setSuperclass(cls);
		e.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				if (isTerminal(method.getReturnType())) {
					return Defaults.defaultValue(method.getReturnType());
				}
				return createDummyProxy(method.getReturnType());
			}
		});

		return e.create();
	}

	@SuppressWarnings("unchecked")
	void addBindingUntyped(AttachedPropertyBearer component,
			Consumer<?> pullUp, Consumer<?> pushDown) {
		addBinding(component, (Consumer<T>) pullUp, (Consumer<T>) pushDown);
	}

	void addBinding(AttachedPropertyBearer component, Consumer<T> pullUp,
			Consumer<T> pushDown) {
		Set<Binding<T>> set = bindings.get(component);
		if (set == null) {
			set = new HashSet<>();
			bindings.set(component, set);
			components.put(component, null);
		}
		set.add(new Binding<T>() {

			@Override
			public void pushDown() {
				if (pushDown != null) {
					pushDown.accept(data);
				}
			}

			@Override
			public void pullUp() {
				if (pullUp != null) {
					pullUp.accept(data);
				}
			}
		});

		if (pullUp != null) {
			pullUp.accept(data);
		}
	}
}
