package com.github.ruediste.rise.crud;

import javax.inject.Inject;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;

public class DefaultCrudDisplayController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @Inject
    CrudReflectionUtil reflectionUtil;

    static class View extends
            DefaultCrudViewComponent<DefaultCrudDisplayController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudDisplayComponents displayComponents;

        @Override
        protected Component createComponents() {
            return toComponent(html -> {
                for (PersistentProperty p : util
                        .getDisplayProperties2(controller.type)) {
                    html.add(displayComponents.create(p, controller.data));
                }
                html.rButtonA(go(CrudControllerBase.class).browse(
                        controller.type.getEntityClass(),
                        controller.type.getEmQualifier()));
                html.rButtonA(go(CrudControllerBase.class).edit(
                        controller.data.get()));
                html.rButtonA(
                        go(CrudControllerBase.class).delete(
                                controller.data.get()), x -> x.danger());

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
