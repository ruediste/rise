package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.Labeled;

public class DefaultCrudEditController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    static class View extends
            DefaultCrudViewComponent<DefaultCrudEditController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudEditComponents editComponents;

        @Override
        protected Component createComponents() {
            return new CComponentStack(
                    toComponent(html -> {
                        for (PropertyDeclaration p : util
                                .getEditProperties(controller.entityGroup.get()
                                        .getClass())) {
                            html.add(editComponents.createEditComponent(p,
                                    controller.entityGroup));
                        }
                        html.add(new CButton(controller, c -> c.save()));
                        html.rButtonA(go(CrudControllerBase.class).browse(
                                controller.entityGroup.get().getClass(),
                                controller.emQualifier));

                    }));
        }
    }

    Class<? extends Annotation> emQualifier;

    BindingGroup<Object> entityGroup;

    Object entity() {
        return entityGroup.proxy();
    }

    public DefaultCrudEditController initialize(Object entity) {
        this.entityGroup = new BindingGroup<>(entity);
        emQualifier = util.getEmQualifier(entity);
        return this;
    }

    @Inject
    EntityManagerHolder holder;

    @Labeled
    @GlyphiconIcon(Glyphicon.save)
    void save() {
        entityGroup.pushDown();
        commit();
        redirect(go(CrudControllerBase.class).display(entityGroup.get()));
    }
}
