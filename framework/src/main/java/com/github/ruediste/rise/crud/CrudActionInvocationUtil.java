package com.github.ruediste.rise.crud;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.github.ruediste.rise.api.InjectParameter;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.ComponentStackHandle;
import com.github.ruediste.rise.component.generic.EditComponentWrapper;
import com.github.ruediste.rise.component.generic.EditComponents;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.reflect.TypeToken;

@Singleton
public class CrudActionInvocationUtil {

	@Inject
	Injector injector;

	@Inject
	EditComponents editComponents;

	@Inject
	Provider<ArgumentController> argumentControllerProvider;

	public interface ActionMethodInvocationOutcome {
		default boolean wasCanceled() {
			return false;
		}
	}

	public void invokeActionMethod(Method m, Object target, ComponentStackHandle stackHandle,
			Consumer<ActionMethodInvocationOutcome> callback) {
		// analyze parameters
		ArrayList<Pair<Component, Parameter>> argumentComponents = new ArrayList<>();
		ArrayList<Supplier<Object>> argumentSuppliers = new ArrayList<>();
		for (Parameter parameter : m.getParameters()) {
			if (parameter.isAnnotationPresent(InjectParameter.class)) {
				argumentSuppliers.add(
						() -> injector.getInstance(DependencyKey.of(TypeToken.of(parameter.getParameterizedType()))));
			} else {
				EditComponentWrapper<?> wrapper = editComponents.type(parameter.getType()).get();
				argumentComponents.add(Pair.of(wrapper.getComponent(), parameter));
				argumentSuppliers.add(() -> wrapper.getValue());
			}
		}

		// create runnable to perform the invocation, but do not yet execute it
		Runnable performInvocation = () -> {
			// invoke action method
			try {
				m.setAccessible(true);
				m.invoke(target, argumentSuppliers.stream().map(x -> x.get()).collect(Collectors.toList()).toArray());
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getCause());
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException("Error while invoking action method " + m, e);
			}

			// process result

			// notify callback
			callback.accept(new ActionMethodInvocationOutcome() {
			});
		};

		if (!argumentComponents.isEmpty()) {
			// show input screen
			ArgumentController ctrl = argumentControllerProvider.get();
			ctrl.initialize(m, argumentComponents, performInvocation, stackHandle, callback);
			stackHandle.pushComponent(new CController(ctrl));
		} else {
			// no user input required, perform invocation
			performInvocation.run();
		}
	}

	static class ArgumentView extends FrameworkViewComponent<ArgumentController> {
		@Inject
		LabelUtil labelUtil;

		@Override
		protected Component createComponents() {
			return toComponent(html -> html.h1().write("Provide Arguments for ")
					.content(labelUtil.method(controller.method).label())
					.fForEach(controller.argumentComponents,
							pair -> html.bFormGroup().label().content(labelUtil.methodParameter(pair.getB()).label())
									.add(pair.getA())._bFormGroup())
					.add(new CButton(controller, x -> x.invoke())).add(new CButton(controller, x -> x.cancel())));
		}

	}

	static class ArgumentController extends SubControllerComponent {

		private Method method;
		private ArrayList<Pair<Component, Parameter>> argumentComponents;
		private Runnable performInvocation;
		private ComponentStackHandle stackHandle;
		private Consumer<ActionMethodInvocationOutcome> callback;

		public void initialize(Method method, ArrayList<Pair<Component, Parameter>> argumentComponents,
				Runnable performInvocation, ComponentStackHandle stackHandle,
				Consumer<ActionMethodInvocationOutcome> callback) {
			this.method = method;
			this.argumentComponents = argumentComponents;
			this.performInvocation = performInvocation;
			this.stackHandle = stackHandle;
			this.callback = callback;
		}

		@Labeled
		public void cancel() {
			stackHandle.popComponent();
			callback.accept(new ActionMethodInvocationOutcome() {

				@Override
				public boolean wasCanceled() {
					return true;
				}
			});
		}

		@Labeled
		public void invoke() {
			stackHandle.popComponent();
			performInvocation.run();
		}

	}
}
