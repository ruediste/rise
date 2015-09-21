package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.Labeled;

public class DefaultCrudCreateController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @Inject
    CrudReflectionUtil reflectionUtil;

    static class View
            extends DefaultCrudViewComponent<DefaultCrudCreateController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudEditComponents editComponents;

        @Override
        protected Component createComponents() {
            return toComponent(html -> {
                for (PersistentProperty p : util
                        .getEditProperties(controller.type)) {
                    html.add(editComponents.createEditComponent(p,
                            controller.type, controller.entityGroup));
                }
                html.add(new CButton(controller, c -> c.create()));
                html.rButtonA(go(CrudControllerBase.class).browse(
                        controller.entityGroup.get().getClass(),
                        controller.emQualifier));

            });
        }
    }

    Class<? extends Annotation> emQualifier;

    BindingGroup<Object> entityGroup;

    Object entity() {
        return entityGroup.proxy();
    }

    PersistentType type;

    public DefaultCrudCreateController initialize(Class<?> entityClass,
            Class<? extends Annotation> emQualifier) {
        type = reflectionUtil.getPersistentType(emQualifier, entityClass);
        try {
            this.entityGroup = new BindingGroup<>(entityClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error while creating instance of "
                    + entityClass + " for default create CRUD controller");
        }
        this.emQualifier = emQualifier;
        return this;
    }

    @Inject
    EntityManagerHolder holder;

    @Labeled
    @GlyphiconIcon(Glyphicon.plus_sign)
    void create() {
        entityGroup.pushDown();
        commit(() -> {
            holder.getEntityManager(emQualifier).persist(entityGroup.get());
        });
        redirect(go(CrudControllerBase.class).display(entityGroup.get()));
    }
}
