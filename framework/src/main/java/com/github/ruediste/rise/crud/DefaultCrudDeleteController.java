package com.github.ruediste.rise.crud;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CButtonTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;

public class DefaultCrudDeleteController extends SubControllerComponent {

    @Inject
    RisePersistenceUtil util;

    @Inject
    CrudReflectionUtil reflectionUtil;

    @Inject
    EntityManagerHolder holder;

    @TMessages
    public interface Messages {
        @TMessage("Really delete {name}")
        LString delete(String name);
    }

    @MembersLabeled
    public enum Labels {
        REALLY_DELETE
    }

    static class View extends
            DefaultCrudViewComponent<DefaultCrudDeleteController> {
        @Inject
        Messages messages;
        @Inject
        CrudReflectionUtil util;

        @Inject
        CrudUtil crudUtil;

        @Override
        protected Component createComponents() {
            return toComponent(html -> {
                html.div().B_BG_DANGER().h1().content(Labels.REALLY_DELETE);

                crudUtil.getStrategy(CrudUtil.IdentificationRenderer.class,
                        controller.type.getClass()).renderIdenification(html,
                        controller.entity);
                html._div();

                html.add(new CButton(controller, c -> c.delete())
                        .apply(CButtonTemplate.setArgs(x -> x.danger())));
                html.rButtonA(go(CrudControllerBase.class).browse(
                        controller.type.getEntityClass(),
                        controller.type.getEmQualifier()));
                html.rButtonA(go(CrudControllerBase.class).display(
                        controller.entity));

            });
        }
    }

    private Object entity;
    private PersistentType type;

    public DefaultCrudDeleteController initialize(Object entity) {
        this.entity = entity;
        type = reflectionUtil.getPersistentType(entity);
        return this;
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.remove_sign)
    void delete() {
        holder.getEntityManager(type.getEmQualifier()).remove(entity);
        commit();
        redirect(go(CrudControllerBase.class).browse(type.getEntityClass(),
                type.getEmQualifier()));
    }
}