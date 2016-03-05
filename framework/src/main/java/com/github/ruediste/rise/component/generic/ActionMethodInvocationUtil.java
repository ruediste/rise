package com.github.ruediste.rise.component.generic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.api.InjectParameter;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.ComponentStackHandle;
import com.github.ruediste.rise.component.generic.ActionMethodInvocationResult.ShowObjectActionMethodInvocationResult;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.nonReloadable.front.reload.MemberOrderIndex;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.DependencyKey;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.reflect.TypeToken;

@Singleton
public class ActionMethodInvocationUtil {

    @Inject
    Injector injector;

    @Inject
    EditComponents editComponents;

    @Inject
    Provider<ArgumentController> argumentControllerProvider;
    @Inject
    Provider<ResultController> resultControllerProvider;

    @Inject
    TransactionControl txc;

    @Inject
    ComponentUtil util;

    public interface ActionMethodInvocationOutcome {
        default boolean wasCanceled() {
            return false;
        }
    }

    public void invokeAction(Method m, Object target, ComponentStackHandle stackHandle,
            Consumer<ActionMethodInvocationOutcome> callback) {
        // analyze parameters
        ArrayList<Pair<Component, Parameter>> argumentComponents = new ArrayList<>();
        ArrayList<Supplier<Object>> argumentSuppliers = new ArrayList<>();
        for (Parameter parameter : m.getParameters()) {
            if (parameter.isAnnotationPresent(InjectParameter.class)) {
                argumentSuppliers.add(
                        () -> injector.getInstance(DependencyKey.of(TypeToken.of(parameter.getParameterizedType()))));
            } else {
                EditComponentWrapper<?> wrapper = editComponents.type(parameter.getType()).testName(parameter.getName())
                        .get();
                argumentComponents.add(Pair.of(wrapper.getComponent(), parameter));
                argumentSuppliers.add(() -> wrapper.getValue());
            }
        }

        // create runnable to perform the invocation, but do not yet execute it
        Runnable performInvocation = () -> {

            // invoke action method
            Supplier<Object> invokeMethod = () -> {
                try {
                    m.setAccessible(true);
                    return m.invoke(target,
                            argumentSuppliers.stream().map(x -> x.get()).collect(Collectors.toList()).toArray());
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException("Error while invoking action method " + m, e);
                }
            };

            Object result;

            if (m.isAnnotationPresent(Updating.class))
                result = txc.updating().execute(() -> invokeMethod.get());
            else
                result = invokeMethod.get();

            // process result
            if (result instanceof ActionMethodInvocationResult.RedirectActionMethodInvocationResult) {
                util.closePage(new RedirectRenderResult(util.toUrlSpec(
                        ((ActionMethodInvocationResult.RedirectActionMethodInvocationResult) result).target)));
            } else if (result instanceof ShowObjectActionMethodInvocationResult) {
                showResult(m, ((ShowObjectActionMethodInvocationResult) result).result, stackHandle, callback);
                return;
            } else if (result != null) {
                showResult(m, result, stackHandle, callback);
                return;
            }

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

    private void showResult(Method m, Object result, ComponentStackHandle stackHandle,
            Consumer<ActionMethodInvocationOutcome> callback) {
        ResultController ctrl = resultControllerProvider.get();
        ctrl.initialize(m, result, stackHandle, callback);
        stackHandle.pushComponent(new CController(ctrl));
    }

    static class ArgumentView extends FrameworkViewComponent<ArgumentController> {
        @Inject
        LabelUtil labelUtil;

        @Override
        protected Component createComponents() {
            return toComponent(html -> html.h1().write("Provide Arguments for ")
                    .content(labelUtil.method(controller.method).label())

                    .div().TEST_NAME("properties")
                    .fForEach(controller.argumentComponents, pair -> html.bFormGroup().label()
                            .content(labelUtil.methodParameter(pair.getB()).label()).add(pair.getA())._bFormGroup())
                    ._div()

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

    static class ResultView extends FrameworkViewComponent<ResultController> {
        @Inject
        LabelUtil labelUtil;

        @Inject
        DisplayRenderers renderers;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected Component createComponents() {
            return toComponent(
                    html -> html.h1().write("Result of ").content(labelUtil.method(controller.method).label())

                            .div().TEST_NAME("properties")
                            .fForEach(controller.properties,
                                    p -> html.bFormGroup().label().content(labelUtil.property(p).label()).span()
                                            .BformControl().DISABLED("disabled").TEST_NAME(p.getName())
                                            .render(((DisplayRenderer) renderers.property(p).get())
                                                    .renderable(p.getValue(controller.result)))
                                            ._span()._bFormGroup())
                            ._div()

                            .add(new CButton(controller, x -> x.ok())));
        }

    }

    static class ResultController extends SubControllerComponent {

        @Inject
        MemberOrderIndex idx;

        private Method method;
        private ComponentStackHandle stackHandle;
        private Consumer<ActionMethodInvocationOutcome> callback;

        Collection<PropertyInfo> properties;

        Object result;

        public void initialize(Method method, Object result, ComponentStackHandle stackHandle,
                Consumer<ActionMethodInvocationOutcome> callback) {
            this.method = method;
            this.result = result;
            this.stackHandle = stackHandle;
            this.callback = callback;
            properties = PropertyUtil.getPropertyInfoMap(result.getClass()).values();
        }

        @Labeled
        public void ok() {
            stackHandle.popComponent();
            callback.accept(new ActionMethodInvocationOutcome() {
            });
        }

    }
}
