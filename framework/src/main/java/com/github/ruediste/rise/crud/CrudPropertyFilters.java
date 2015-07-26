package com.github.ruediste.rise.crud;

import java.lang.reflect.Type;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
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
import com.google.common.reflect.TypeToken;

@Singleton
public class CrudPropertyFilters extends
        FactoryCollection<PropertyDeclaration, CrudPropertyFilter> {

    @Inject
    LabelUtil labelUtil;

    @Inject
    ComponentFactoryUtil componentFactoryUtil;

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
                new Function<PropertyDeclaration, CrudPropertyFilter>() {
                    @Override
                    public CrudPropertyFilter apply(PropertyDeclaration decl) {

                        Type type = decl.getPropertyType();
                        Class<?> rawType = TypeToken.of(type).getRawType();

                        if (String.class.equals(type)) {
                            CTextField textField = new CTextField().setText("")
                                    .setLabel(labelUtil.getPropertyLabel(decl));
                            return new CrudPropertyFilter() {

                                @Override
                                public Component getComponent() {
                                    return textField;
                                }

                                @Override
                                public void applyFilter(
                                        CrudFilterPersitenceContext ctx) {
                                    ctx.addWhere(ctx.cb().like(
                                            ctx.root().get(decl.getName()),
                                            "%" + textField.getText() + "%"));
                                }
                            };
                        }

                        if (Long.class.equals(Primitives.wrap(rawType))) {
                            LString propertyLabel = labelUtil
                                    .getPropertyLabel(decl);
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
                                .label().content(labelUtil.getPropertyLabel(decl))
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
                                                        ctx.root().get(
                                                                decl.getName()),
                                                        Long.parseLong(min
                                                                .getValue())));

                                    }
                                    if (!Strings.isNullOrEmpty(max.getValue())) {
                                        ctx.addWhere(ctx
                                                .cb()
                                                .lessThanOrEqualTo(
                                                        ctx.root().get(
                                                                decl.getName()),
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
