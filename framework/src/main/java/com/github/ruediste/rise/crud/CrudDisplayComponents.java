package com.github.ruediste.rise.crud;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste1.i18n.label.LabelUtil;

@Singleton
public class CrudDisplayComponents
        extends
        FactoryCollection<Pair<PropertyDeclaration, Object>, CrudDisplayComponent> {

    @Inject
    ComponentFactoryUtil util;
    @Inject
    LabelUtil labelUtil;

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    public CrudDisplayComponents() {
        getFactories().add(
                pair -> {
                    PropertyDeclaration decl = pair.getA();
                    Object entity = pair.getB();
                    if (Long.TYPE.equals(decl.getPropertyType())
                            || Long.class.equals(decl.getPropertyType())
                            || String.class.equals(decl.getPropertyType())) {
                        return new CrudDisplayComponent() {

                            @Override
                            public Component getComponent() {
                                return toComponent(html -> html
                                        .bFormGroup()
                                        .label()
                                        .content(
                                                labelUtil
                                                        .getPropertyLabel(decl))
                                        .span()
                                        .B_FORM_CONTROL()
                                        .DISABLED("disabled")
                                        .content(
                                                String.valueOf(decl
                                                        .getValue(entity)))
                                        ._bFormGroup());

                            }
                        };
                    }

                    return null;
                });
    }
}
