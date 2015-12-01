package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.List;

/**
 * Component displaying a list of items and allowing to reorder them
 */
@DefaultTemplate(CSortableTemplate.class)
public class CSortable<T> extends CItems<T, CSortable<T>> {

    /**
     * Reorder the items in the given order
     */
    public void applyItemOrder(List<Integer> idxList) {
        setItems(idxList.stream().map(idx -> getItems().get(idx))
                .collect(toList()));
    }

}
