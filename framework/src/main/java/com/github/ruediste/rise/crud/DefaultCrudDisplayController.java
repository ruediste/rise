package com.github.ruediste.rise.crud;

import java.lang.reflect.Method;

import javax.inject.Inject;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CIconLabel;
import com.github.ruediste.rise.component.components.ComponentStackHandle;
import com.github.ruediste.rise.component.generic.ActionMethodInvocationUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.integration.IconUtil;
import com.github.ruediste1.i18n.label.LabelUtil;

public class DefaultCrudDisplayController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @Inject
    CrudReflectionUtil reflectionUtil;

    @Inject
    CrudUtil crudUtil;

    static class View extends FrameworkViewComponent<DefaultCrudDisplayController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudDisplayComponents displayComponents;

        @Inject
        LabelUtil labelUtil;

        @Inject
        IconUtil iconUtil;

        @Override
        protected Component createComponents() {
            return new CComponentStack(toComponent(html -> {
                html.div().TEST_NAME("properties").fForEach(util.getDisplayProperties(controller.type), p -> {
                    CrudPropertyHandle handle = CrudPropertyHandle.create(p, () -> controller.data.get(),
                            () -> controller.data.proxy(), controller.data);
                    html.bFormGroup().label().content(labelUtil.property(handle.info().getProperty()).label())
                            .add(displayComponents.create(handle))._bFormGroup();
                })._div();

                html.div().TEST_NAME("actions")
                        .fForEach(util.getDisplayActionMethods(controller.type.getEntityClass()), m -> {
                            CButton btn = new CButton().TEST_NAME(m.getName()).add(new CIconLabel().setMethod(m));
                            btn.setHandler(() -> controller.invokeActionMethod(m, new ComponentStackHandle(btn)));
                            html.add(btn);
                        })._div();

                html.div().TEST_NAME("buttons").rButtonA(go(CrudControllerBase.class)
                        .browse(controller.type.getEntityClass(), controller.type.getEmQualifier()));
                html.rButtonA(go(CrudControllerBase.class).edit(controller.data.get()));
                html.rButtonA(go(CrudControllerBase.class).delete(controller.data.get()), x -> x.danger());
                html._div();

            }));
        }

    }

    @Inject
    BindingGroup<Object> data;

    PersistentType type;

    @Inject
    ActionMethodInvocationUtil actionUtil;

    public DefaultCrudDisplayController initialize(Object entity) {
        type = reflectionUtil.getPersistentType(entity);
        data.initialize(entity);
        return this;
    }

    public void invokeActionMethod(Method m, ComponentStackHandle handle) {
        actionUtil.invokeAction(m, data.get(), handle, x -> data.pullUp());
    }

}
