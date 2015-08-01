package com.github.ruediste.rise.crud;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.label.LabelUtil;
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

    private Component toComponentBound(Supplier<?> bindingAccessor,
            Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponentBound(bindingAccessor, renderer);
    }

    public interface CrudDisplayComponentFactory {
        Component create(PersistentProperty property, BindingGroup<?> group);
    }

    public Component create(PersistentProperty property, BindingGroup<?> group) {
        return getFactory(property).create(property, group);
    }

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
                          .span().B_FORM_CONTROL().DISABLED("disabled")
                            .content(String.valueOf(property.getProperty().getValue(group.get())))
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
                            PluralAttribute<?, ?, ?> attribute = (PluralAttribute<?, ?, ?>) p
                                            .getAttribute();
                            html.bFormGroup()
                                .label().content(labelUtil.getPropertyLabel(p.getProperty()))
                                .span().B_FORM_CONTROL().DISABLED("disabled")
                                  .render(x-> crudUtil
                                          .getStrategy(
                                                  IdentificationRenderer.class,
                                                  attribute.getElementType().getJavaType()).renderIdenification(
                                                  html, p.getProperty().getValue(g.get())))
                                ._span()
                            ._bFormGroup();
                     });
                    //@formatter:on
                });

    }
}
