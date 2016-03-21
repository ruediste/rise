package com.github.ruediste.rise.component.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.binding.TwoWayBindingTransformer;
import com.github.ruediste.rise.component.binding.transformers.Transformers;
import com.github.ruediste.rise.component.components.CCheckBox;
import com.github.ruediste.rise.component.components.CInput;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.components.InputType;
import com.github.ruediste.rise.component.tree.Component;
import com.google.common.primitives.Primitives;

@Singleton
public class DefaultEditComponentFactory implements EditComponentFactory {

    @Inject
    Transformers transformers;

    private List<EditComponentFactory> factories = new ArrayList<>();

    @PostConstruct
    void postConstruct() {
        addTransformerFactory(Integer.class, InputType.number, transformers.intToStringTransformer);
        addTransformerFactory(Long.class, InputType.number, transformers.longToStringTransformer);
        addTransformerFactory(Short.class, InputType.number, transformers.shortToStringTransformer);
        addStringTransformerFactory(byte[].class, transformers.byteArrayToHexStringTransformer);
        addStringTransformerFactory(String.class, transformers.identityTransformer());
        addCheckBoxFactory();
    }

    private void addCheckBoxFactory() {

        factories.add((cls, testName, info) -> {
            if (Boolean.class.equals(Primitives.wrap(cls))) {
                CCheckBox checkBox = new CCheckBox();
                return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<Boolean>() {

                    @Override
                    public Component getComponent() {
                        return checkBox;
                    }

                    @Override
                    public Boolean getValue() {
                        return checkBox.isChecked();
                    }

                    @Override
                    public EditComponentWrapper<Boolean> setValue(Boolean value) {
                        checkBox.setChecked(value);
                        return this;
                    }

                    @Override
                    public EditComponentWrapper<Boolean> bindValue(Supplier<Boolean> accessor) {
                        checkBox.bindLabelProperty(c -> c.setChecked(accessor.get()));
                        return this;
                    }

                });
            } else
                return Optional.empty();
        });
    }

    private <T> void addTransformerFactory(Class<T> propertyType, InputType inputType,
            TwoWayBindingTransformer<T, String> transformer) {
        factories.add((cls, testName, info) -> {
            if (propertyType.equals(Primitives.wrap(cls))) {
                CInput component = new CInput(inputType);
                testName.ifPresent(x -> component.TEST_NAME(x));
                return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<T>() {

                    @Override
                    public Component getComponent() {
                        return component;
                    }

                    @Override
                    public T getValue() {
                        return transformer.transformInv(component.getValue());
                    }

                    @Override
                    public EditComponentWrapper<T> setValue(T value) {
                        component.setValue(transformer.transform(value));
                        return this;
                    }

                    @Override
                    public EditComponentWrapper<T> bindValue(Supplier<T> accessor) {
                        component.bindValue(() -> transformer.transform(accessor.get()));
                        return this;
                    }
                });
            }
            return Optional.empty();
        });

    }

    private <T> void addStringTransformerFactory(Class<T> propertyType,
            TwoWayBindingTransformer<T, String> transformer) {
        factories.add((cls, testName, info) -> {
            if (propertyType.equals(Primitives.wrap(cls))) {
                CTextField component = new CTextField();
                testName.ifPresent(x -> component.TEST_NAME(x));
                return Optional.<EditComponentWrapper<?>> of(new EditComponentWrapper<T>() {

                    @Override
                    public Component getComponent() {
                        return component;
                    }

                    @Override
                    public T getValue() {
                        return transformer.transformInv(component.getText());
                    }

                    @Override
                    public EditComponentWrapper<T> setValue(T value) {
                        component.setText(transformer.transform(value));
                        return this;
                    }

                    @Override
                    public EditComponentWrapper<T> bindValue(Supplier<T> accessor) {
                        component.bindText(() -> transformer.transform(accessor.get()));
                        return this;
                    }
                });
            }
            return Optional.empty();
        });

    }

    @Override
    public Optional<EditComponentWrapper<?>> getComponent(Class<?> cls, Optional<String> name,
            Optional<PropertyInfo> info) {
        return factories.stream().map(f -> f.getComponent(cls, name, info)).filter(Optional::isPresent)
                .<EditComponentWrapper<?>> map(x -> x.get()).findFirst();
    }

}
