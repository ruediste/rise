package com.github.ruediste.rise.crud;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentTreeUtil;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.primitives.Primitives;

@Singleton
public class CrudDisplayComponents
        extends
        FactoryCollectionNew<PersistentProperty, CrudDisplayComponents.CrudDisplayComponentFactory> {

    @Inject
    ComponentFactoryUtil util;

    @Inject
    LabelUtil labelUtil;

    @Inject
    CrudUtil crudUtil;

    @Inject
    RisePersistenceUtil persistenceUtil;

    @Inject
    ComponentUtil componentUtil;

    private Component toComponentBound(Supplier<?> bindingAccessor,
            Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponentBound(bindingAccessor, renderer);
    }

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    public interface CrudDisplayComponentFactory {
        Component create(PersistentProperty property, BindingGroup<?> group);
    }

    public Component create(PersistentProperty property, BindingGroup<?> group) {
        return getFactory(property).create(property, group);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CrudDisplayComponents() {
        addFactory(
                p -> Long.class.equals(Primitives.wrap(p.getAttribute()
                        .getJavaType()))
                        || String.class.equals(p.getAttribute().getJavaType()),
                (property, group) -> {
                    return toComponentBound(()->group.proxy(),html -> html
                        //@formatter:off
                        .bFormGroup()
                          .label().content(labelUtil.getPropertyLabel(property.getProperty()))
                          .span().BformControl().DISABLED("disabled").TEST_NAME(property.getName())
                            .content(String.valueOf(property.getValue(group.get())))
                        ._bFormGroup());
                        //@formatter:on
                });

        addFactory(
                p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE,
                (p, g) -> {
                    //@formatter:off
                    return toComponentBound(
                            () -> g.proxy(),
                            html -> {
                                Object value = p.getValue(g.get());
                                html.bFormGroup()
                                    .label().content(labelUtil.getPropertyLabel(p.getProperty()))
                                        .fIf(value!=null, ()->html.bInputGroup())
                                        .span().BformControl().DISABLED("disabled").TEST_NAME(p.getName())
                                        .render(x-> crudUtil .getStrategy(IdentificationRenderer.class, p.getAttribute().getJavaType())
                                          .renderIdenification(html, value))
                                        ._span()
                                        .fIf(value!=null, ()->html
                                            .bInputGroupBtn()
                                                .rButtonA(componentUtil.go(CrudControllerBase.class).display(value), true)
                                            ._bInputGroupBtn()
                                            ._bInputGroup()
                                        )
                                ._bFormGroup();
                            });
                    //@formatter:on
                });
        //@formatter:off
        addFactory(
                p -> p.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY,
                (p, g) -> toComponent(html->html
                        .bFormGroup()
                            .label().content(labelUtil.getPropertyLabel(p.getProperty()))
                            .div()//.B_FORM_CONTROL()
                                .add(new CButton(this,
                                (btn, x) -> x.showItems(() -> {
                                    Object entity = g.get();
                                    PluralAttribute attr=(PluralAttribute) p.getAttribute();

                                    CrudList list =
                                        crudUtil
                                        .getStrategy(CrudUtil.CrudListFactory.class,entity.getClass())
                                        .createList(
                                            persistenceUtil.getEmQualifier(entity),
                                            attr.getElementType().getJavaType(),
                                            ctx -> {
                                                Subquery<? extends Object> sq = ctx.query()
                                                        .subquery(entity.getClass());
                                                Root<? extends Object> root = sq.from(entity
                                                        .getClass());
                                                ctx.addWhere(ctx.root().in(
                                                        root.get((PluralAttribute) p
                                                                .getAttribute())));
                                            }
                                        );
                                    list.setItemActionsFactory(obj->new CDataGrid.Cell(
                                            new CButton(componentUtil.go(CrudControllerBase.class).display(obj))
                                            ));
                                    list.setBottomActions(new CButton(
                                            this,
                                            x1 -> x1.back(() -> ComponentTreeUtil
                                                    .raiseEvent(
                                                            btn,
                                                            new CComponentStack.PopComponentEvent()))));

                                    ComponentTreeUtil.raiseEvent(btn,
                                            new CComponentStack.PushComponentEvent(
                                                    new CController(list)));
                                })))
                            ._div()
                        ._bFormGroup()));
        //@formatter:off
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.eye_open)
    void showItems(Runnable callback) {
        callback.run();
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.arrow_left)
    void back(Runnable callback) {
        callback.run();
    }
}
