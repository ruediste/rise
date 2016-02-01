package com.github.ruediste.rise.crud;

import javax.inject.Inject;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste1.i18n.label.LabelUtil;

public class DefaultCrudDisplayController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @Inject
    CrudReflectionUtil reflectionUtil;

    static class View
            extends FrameworkViewComponent<DefaultCrudDisplayController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudDisplayComponents displayComponents;

        @Inject
        LabelUtil labelUtil;

        @Override
        protected Component createComponents() {
            return toComponent(html -> {
                html.div().TEST_NAME("properties");
                for (CrudPropertyInfo p : util
                        .getDisplayProperties(controller.type)) {
                    CrudPropertyHandle handle = CrudPropertyHandle.create(p,
                            () -> controller.data.get(),
                            () -> controller.data.proxy(), controller.data);
                    html.bFormGroup().label()
                            .content(labelUtil.getPropertyLabel(
                                    handle.info().getProperty()))
                            .add(displayComponents.create(handle))
                            ._bFormGroup();
                }
                html._div().div().TEST_NAME("buttons")
                        .rButtonA(go(CrudControllerBase.class).browse(
                                controller.type.getEntityClass(),
                                controller.type.getEmQualifier()));
                html.rButtonA(go(CrudControllerBase.class)
                        .edit(controller.data.get()));
                html.rButtonA(go(CrudControllerBase.class)
                        .delete(controller.data.get()), x -> x.danger());
                html._div();

            });
        }
    }

    BindingGroup<Object> data;

    PersistentType type;

    public DefaultCrudDisplayController initialize(Object entity) {
        type = reflectionUtil.getPersistentType(entity);
        data = new BindingGroup<>(entity);
        return this;
    }
}
