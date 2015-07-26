package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
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
            return toComponent(html -> {
                for (PropertyDeclaration p : util
                        .getEditProperties(controller.entity.get().getClass())) {
                    html.add(editComponents.create(p).createComponent(
                            controller.entity));
                }
                html.add(new CButton(controller, c -> c.save()));
                html.rButtonA(go(CrudControllerBase.class).browse(
                        controller.entity.get().getClass(),
                        controller.emQualifier));

            });
        }
    }

    Class<? extends Annotation> emQualifier;

    BindingGroup<Object> entity = new BindingGroup<>(Object.class);

    Object entity() {
        return entity.proxy();
    }

    public DefaultCrudEditController initialize(Object entity) {
        this.entity.set(entity);
        emQualifier = util.getEmQualifier(entity);
        return this;
    }

    @Inject
    EntityManagerHolder holder;

    @Labeled
    @GlyphiconIcon(Glyphicon.save)
    void save() {
        entity.pushDown();
        commit();
        redirect(go(CrudControllerBase.class).display(entity.get()));
    }
}
