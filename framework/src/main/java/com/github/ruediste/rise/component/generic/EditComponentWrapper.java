package com.github.ruediste.rise.component.generic;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;

public interface EditComponentWrapper<T> {

    Component getComponent();

    T getValue();

    EditComponentWrapper<T> setValue(T value);

    EditComponentWrapper<T> bindValue(Supplier<T> accessor);

}
