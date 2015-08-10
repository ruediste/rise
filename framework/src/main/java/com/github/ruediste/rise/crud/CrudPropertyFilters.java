package com.github.ruediste.rise.crud;

import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;
import com.google.common.base.Strings;
import com.google.common.primitives.Primitives;

@Singleton
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CrudPropertyFilters extends
        FactoryCollection<PersistentProperty, CrudPropertyFilter> {

    @Inject
    LabelUtil labelUtil;

    @Inject
    ComponentFactoryUtil componentFactoryUtil;

    @Inject
    CrudReflectionUtil reflectionUtil;

    @TMessages
    public interface Messages {
        @TMessage("Minimum of {property}")
        PatternString minLong(LString property);

        @TMessage("Maximum of {property}")
        PatternString maxLong(LString property);

        TranslatedString min();

        TranslatedString max();
    }

    @Inject
    Messages messages;

    {
        getFactories().add(
                new Function<PersistentProperty, CrudPropertyFilter>() {

                    @Override
                    public CrudPropertyFilter apply(PersistentProperty decl) {

                        Class<?> cls = decl.getAttribute().getJavaType();

                        PropertyInfo property = decl.getProperty();

                        if (String.class.equals(cls)) {
                            CTextField textField = new CTextField()
                                    .setText("")
                                    .TEST_NAME(property.getName())
                                    .setLabel(
                                            labelUtil
                                                    .getPropertyLabel(property));
                            CFormGroup component = new CFormGroup(textField);
                            return new CrudPropertyFilter() {

                                @Override
                                public Component getComponent() {
                                    return component;
                                }

                                @Override
                                public void applyFilter(
                                        CrudFilterPersitenceContext ctx) {
                                    CriteriaBuilder cb = ctx.cb();
                                    Path<String> path = ctx.root().get(
                                            (SingularAttribute) decl
                                                    .getAttribute());
                                    ctx.addWhere(cb.or(
                                            path.isNull(),
                                            cb.like(path,
                                                    "%" + textField.getText()
                                                            + "%")));
                                }
                            };
                        }

                        if (Long.class.equals(Primitives.wrap(cls))) {
                            LString propertyLabel = labelUtil
                                    .getPropertyLabel(property);
                            CInput min = new CInput(InputType.number)
                                    .setLabel(messages.minLong(propertyLabel))
                                    .setValue("").setRenderFormGroup(false);

                            CInput max = new CInput(InputType.number)
                                    .setLabel(messages.maxLong(propertyLabel))
                                    .setValue("").setRenderFormGroup(false);

                            // @formatter:off
                            Component component = componentFactoryUtil.toComponent((BootstrapRiseCanvas<?> html) -> 
                              html
                              .bFormGroup()
                                .label().content(labelUtil.getPropertyLabel(property))
                                  .div().B_FORM_INLINE()
                                    .div().B_INPUT_GROUP().CLASS(x->x.sm(6))
                                      .span().B_INPUT_GROUP_ADDON().content(messages.min())
                                      .add(min)
                                    ._div()
                                    .div().B_INPUT_GROUP().CLASS(x->x.sm(6))
                                      .span().B_INPUT_GROUP_ADDON().content(messages.max())
                                      .add(max)
                                    ._div()
                                ._div()
                              ._bFormGroup());
                            // @formatter:on

                            return new CrudPropertyFilter() {

                                @Override
                                public Component getComponent() {
                                    return component;
                                }

                                @Override
                                public void applyFilter(
                                        CrudFilterPersitenceContext ctx) {
                                    if (!Strings.isNullOrEmpty(min.getValue())) {
                                        ctx.addWhere(ctx
                                                .cb()
                                                .greaterThanOrEqualTo(
                                                        ctx.root()
                                                                .get(decl
                                                                        .getProperty()
                                                                        .getName()),
                                                        Long.parseLong(min
                                                                .getValue())));

                                    }
                                    if (!Strings.isNullOrEmpty(max.getValue())) {
                                        ctx.addWhere(ctx
                                                .cb()
                                                .lessThanOrEqualTo(
                                                        ctx.root()
                                                                .get(decl
                                                                        .getProperty()
                                                                        .getName()),
                                                        Long.parseLong(max
                                                                .getValue())));

                                    }

                                }
                            };
                        }
                        return null;
                    }
                });
    }

}
