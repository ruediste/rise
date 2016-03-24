package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.github.ruediste.rise.component.tree.Component;

/**
 * Component displaying a list of items and allowing to reorder them
 */
@DefaultTemplate(CSortableTemplate.class)
public class CSortable<T> extends CItems<T, Component, CSortable<T>> {

    public CSortable() {
        setChildComponentFactory(i -> new CText(String.valueOf(i)));
    }

    /**
     * Reorder the items in the given order
     */
    public void applyItemOrder(List<Integer> idxList) {
        setItems(idxList.stream().map(idx -> getItems().get(idx)).collect(toList()));
    }

}
