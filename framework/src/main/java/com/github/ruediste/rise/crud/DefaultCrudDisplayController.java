package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.util.Pair;

public class DefaultCrudDisplayController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    static class View extends
            DefaultCrudViewComponent<DefaultCrudDisplayController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudDisplayComponents displayComponents;

        @Override
        protected Component createComponents() {
            return toComponent(html -> {
                for (PropertyDeclaration p : util
                        .getDisplayProperties(controller.data().getEntity()
                                .getClass())) {
                    html.add(displayComponents.getFactory(
                            Pair.of(p, controller.data().getEntity()))
                            .getComponent());
                }
                html.rButtonA(go(CrudControllerBase.class).browse(
                        controller.data().getEntity().getClass(),
                        controller.data().getEmQualifier()));
                html.rButtonA(go(CrudControllerBase.class).edit(
                        controller.data().getEntity()));
                html.rButtonA(
                        go(CrudControllerBase.class).delete(
                                controller.data().getEntity()), x -> x.danger());

            });
        }
    }

    static class Data {
        private Object entity;
        private Class<? extends Annotation> emQualifier;

        public Object getEntity() {
            return entity;
        }

        public void setEntity(Object entity) {
            this.entity = entity;
        }

        public Class<? extends Annotation> getEmQualifier() {
            return emQualifier;
        }

        public void setEmQualifier(Class<? extends Annotation> emQualifier) {
            this.emQualifier = emQualifier;
        }

    }

    BindingGroup<Data> data = new BindingGroup<>(new Data());

    Data data() {
        return data.proxy();
    }

    public DefaultCrudDisplayController initialize(Object entity) {
        data.get().setEmQualifier(util.getEmQualifier(entity));
        data.get().setEntity(entity);
        return this;
    }
}
