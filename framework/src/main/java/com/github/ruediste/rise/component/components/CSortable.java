package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;

@DefaultTemplate(CSortableTemplate.class)
public class CSortable<T> extends ComponentBase<CSortable<T>> {

    private Function<T, Component> childComponentFactory;

    private Map<T, Component> children = Collections.emptyMap();

    private List<T> items = Collections.emptyList();

    boolean itemsDirty;

    @Override
    public Iterable<Component> getChildren() {
        updateChildren();
        return getItems().stream().map(children::get).collect(toList());
    }

    void updateChildren() {
        if (itemsDirty) {
            Map<T, Component> newChildren = new HashMap<>();
            for (T item : getItems()) {
                Component child = children.get(item);
                if (child == null) {
                    child = getChildComponentFactory().apply(item);
                    child.parentChanged(this);
                }
                newChildren.put(item, child);
            }
            children = newChildren;
            itemsDirty = false;
        }

    }

    @Override
    public void childRemoved(Component child) {
        throw new UnsupportedOperationException();
    }

    public List<T> getItems() {
        return items;
    }

    public CSortable<T> setItems(List<T> items) {
        itemsDirty = true;
        this.items = new ArrayList<>(items);
        return this;
    }

    public Function<T, Component> getChildComponentFactory() {
        return childComponentFactory;
    }

    public CSortable<T> setChildComponentFactory(
            Function<T, Component> childComponentFactory) {
        this.childComponentFactory = childComponentFactory;
        return this;
    }

    /**
     * Reorder the items in the given order
     */
    public void applyItemOrder(List<Integer> idxList) {
        items = idxList.stream().map(idx -> items.get(idx)).collect(toList());
    }

}
