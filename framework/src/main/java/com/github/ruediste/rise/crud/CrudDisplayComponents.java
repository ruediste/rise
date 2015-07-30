package com.github.ruediste.rise.crud;

import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.ManyToOne;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.crud.CrudUtil.IdentificationRenderer;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.reflect.TypeToken;

@Singleton
public class CrudDisplayComponents
        extends
        FactoryCollection<Pair<PropertyDeclaration, Object>, CrudDisplayComponent> {

    @Inject
    ComponentFactoryUtil util;

    @Inject
    LabelUtil labelUtil;

    @Inject
    CrudUtil crudUtil;

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    public CrudDisplayComponents() {
        getFactories()
                .add(new Function<Pair<PropertyDeclaration, Object>, CrudDisplayComponent>() {
                    @Override
                    public CrudDisplayComponent apply(
                            Pair<PropertyDeclaration, Object> pair) {
                        PropertyDeclaration decl = pair.getA();
                        Object entity = pair.getB();
                        if (Long.TYPE.equals(decl.getPropertyType())
                                || Long.class.equals(decl.getPropertyType())
                                || String.class.equals(decl.getPropertyType())) {
                            return new CrudDisplayComponent() {

                                @Override
                                public Component getComponent() {
                                    return toComponent(html -> html
                                        //@formatter:off
                                        .bFormGroup()
                                          .label().content(labelUtil.getPropertyLabel(decl))
                                          .span().B_FORM_CONTROL().DISABLED("disabled")
                                            .content(String.valueOf(decl.getValue(entity)))
                                        ._bFormGroup());
                                        //@formatter:on
                                }
                            };
                        }

                        if (decl.getBackingField().isAnnotationPresent(
                                ManyToOne.class)) {
                            Object value = decl.getValue(entity);
                            Class<?> cls;
                            if (value == null)
                                cls = TypeToken.of(decl.getPropertyType())
                                        .getRawType();
                            else
                                cls = value.getClass();
                            return new CrudDisplayComponent() {

                                @Override
                                public Component getComponent() {
                                    return toComponent(html -> {
                                        //@formatter:off
                                        html.bFormGroup()
                                        .label().content(labelUtil.getPropertyLabel(decl))
                                        .span().B_FORM_CONTROL().DISABLED("disabled")
                                          .render(x-> crudUtil
                                                  .getStrategy(
                                                          IdentificationRenderer.class,
                                                          cls).renderIdenification(
                                                          html, decl.getValue(entity)))
                                        ._span()
                                      ._bFormGroup();
                                        //@formatter:on
                                    });
                                }
                            };
                        }

                        return null;
                    }
                });
    }
}
