package com.github.ruediste.rise.crud;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste1.i18n.label.LabelUtil;

@Singleton
public class CrudEditComponents extends
        FactoryCollection<PropertyDeclaration, CrudEditComponent> {

    @Inject
    ComponentFactoryUtil util;
    @Inject
    LabelUtil labelUtil;

    private Component toComponent(Renderable<BootstrapRiseCanvas<?>> renderer) {
        return util.toComponent(renderer);
    }

    public CrudEditComponents() {
        getFactories().add(decl -> {

            if (String.class.equals(decl.getPropertyType())) {
                return new CrudEditComponent() {

                    @Override
                    public Component createComponent(BindingGroup<?> group) {
                        CTextField result = new CTextField().setLabel(labelUtil
                                .getPropertyLabel(decl));

                        BindingUtil.bind(result, group,
                                entity -> result.setText(String.valueOf(decl
                                        .getValue(entity))), entity -> decl
                                        .setValue(entity, result.getText()));
                        return result;
                    }
                };
            }
            if (Long.TYPE.equals(decl.getPropertyType())
                    || Long.class.equals(decl.getPropertyType())) {
                return new CrudEditComponent() {

                    @Override
                    public Component createComponent(BindingGroup<?> group) {
                        CInput result = new CInput(InputType.number)
                                .setLabel(labelUtil.getPropertyLabel(decl));

                        BindingUtil.bind(result, group,
                                entity -> result.setValue(String.valueOf(decl
                                        .getValue(entity))), entity -> decl
                                        .setValue(entity, Long.parseLong(result
                                                .getValue())));
                        return result;
                    }
                };
            }

            return null;
        });
    }
}
