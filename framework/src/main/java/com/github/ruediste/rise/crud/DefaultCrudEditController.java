package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonStyle;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.ButtonStyle;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;

public class DefaultCrudEditController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @Inject
    CrudReflectionUtil reflectionUtil;

    static class View extends FrameworkViewComponent<DefaultCrudEditController> {
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudEditComponents editComponents;

        @Inject
        LabelUtil labelUtil;

        @Override
        protected Component createComponents() {
            // @formatter:off
            return toComponent(html -> {
                html.div().TEST_NAME("properties");
                for (CrudPropertyInfo p : util.getEditProperties(controller.type)) {
                    CrudPropertyHandle hadle = CrudPropertyHandle.create(p, () -> controller.entityGroup.get(),
                            () -> controller.entityGroup.proxy(), controller.entityGroup);
                    html.bFormGroup().label().content(labelUtil.property(p.getProperty()).label())
                            .add(editComponents.createEditComponent(hadle))._bFormGroup();
                }
                html._div().div().TEST_NAME("buttons").add(new CButton(controller, c -> c.save()))
                        .rButtonA(go(CrudControllerBase.class).display(controller.entityGroup.get()))
                        .rButtonA(go(CrudControllerBase.class).browse(controller.entityGroup.get().getClass(),
                                controller.emQualifier))
                        ._div();

            });
            // @formatter:on
        }
    }

    Class<? extends Annotation> emQualifier;

    @Inject
    BindingGroup<Object> entityGroup;

    Object entity() {
        return entityGroup.proxy();
    }

    PersistentType type;

    public DefaultCrudEditController initialize(Object entity) {
        type = reflectionUtil.getPersistentType(entity);
        this.entityGroup.initialize(entity);
        emQualifier = util.getEmQualifier(entity);
        return this;
    }

    @Inject
    EntityManagerHolder holder;

    @Labeled
    @GlyphiconIcon(Glyphicon.save)
    @ButtonStyle(B_ButtonStyle.PRIMARY)
    public void save() {
        entityGroup.pushDown();
        commit();
        redirect(go(CrudControllerBase.class).display(entityGroup.get()));
    }
}
