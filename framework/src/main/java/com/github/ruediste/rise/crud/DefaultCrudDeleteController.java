package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;

public class DefaultCrudDeleteController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @TMessages
    public interface Messages {
        @TMessage("Really delete {name}")
        LString delete(String name);
    }

    static class View extends
            DefaultCrudViewComponent<DefaultCrudDeleteController> {
        @Inject
        Messages messages;
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudEditComponents editComponents;

        @Override
        protected Component createComponents() {
            return toComponent(html -> {
                html.div()
                        .B_BG_DANGER()
                        .content(
                                messages.delete(controller.entityGroup.get()
                                        .getClass().getName()));

                html.add(new CButton(controller, c -> c.delete()));
                html.rButtonA(go(CrudControllerBase.class).browse(
                        controller.entityGroup.get().getClass(),
                        controller.emQualifier));
                html.rButtonA(
                        go(CrudControllerBase.class).display(
                                controller.entityGroup.get()), x -> x.danger());

            });
        }
    }

    Class<? extends Annotation> emQualifier;

    BindingGroup<Object> entityGroup;

    Object entity() {
        return entityGroup.proxy();
    }

    public DefaultCrudDeleteController initialize(Object entity) {
        this.entityGroup = new BindingGroup<>(entity);
        this.emQualifier = util.getEmQualifier(entity);
        return this;
    }

    @Inject
    EntityManagerHolder holder;

    @Labeled
    @GlyphiconIcon(Glyphicon.remove_sign)
    void delete() {
        Object entity = entityGroup.get();
        Class<? extends Object> entityClass = entity.getClass();
        holder.getEntityManager(emQualifier).remove(entity);
        commit();
        redirect(go(CrudControllerBase.class).browse(entityClass, emQualifier));
    }
}
