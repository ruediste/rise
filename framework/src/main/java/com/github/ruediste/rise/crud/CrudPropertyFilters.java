package com.github.ruediste.rise.crud;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.base.Strings;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

@Singleton
public class CrudPropertyFilters {

    @Inject
    LabelUtil labelUtil;

    @Inject
    ComponentFactoryUtil componentFactoryUtil;

    private final ArrayList<Function<PropertyDeclaration, CrudPropertyFilter>> filterFactories = new ArrayList<>();

    {
        filterFactories
                .add(new Function<PropertyDeclaration, CrudPropertyFilter>() {
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
                            CInput min = new CInput(InputType.number).setLabel(
                                    labelUtil.getPropertyLabel(decl)).setValue(
                                    "");

                            CInput max = new CInput(InputType.number).setLabel(
                                    labelUtil.getPropertyLabel(decl)).setValue(
                                    "");

                            // @formatter:off
                            Component component = componentFactoryUtil.toComponent((BootstrapRiseCanvas<?> html) -> 
                              html.
                              bRow()
                                .bCol(x -> x.xs(12)).div().CLASS("input-group")
                                  .span().CLASS("input-group-addon").content("Min")
                                  .add(min)
                                ._div()._bCol()
                                .bCol(x -> x.xs(6)).div().CLASS("input-group")
                                  .span().CLASS("input-group-addon").content("Max")
                                  .add(max)
                                ._div()._bCol()
                              ._bRow());
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

    public ArrayList<Function<PropertyDeclaration, CrudPropertyFilter>> getFilterFactories() {
        return filterFactories;
    }

    public CrudPropertyFilter createPropertyFilter(PropertyDeclaration decl) {
        return filterFactories
                .stream()
                .map(x -> x.apply(decl))
                .filter(x -> x != null)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(
                                "No FilterFactory found for " + decl));
    }
}
