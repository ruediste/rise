package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.util.Pair;

/**
 * Base class for components displaying a set of items. Null items are not
 * allowed.
 */
public class CItems<T, TSelf extends CItems<T, TSelf>>
        extends ComponentBase<TSelf> {
    private Function<T, Component> childComponentFactory = i -> new CText(
            String.valueOf(i));
    private Function<T, String> testNameExtractor;

    private Map<T, Component> children = Collections.emptyMap();

    private List<T> items = Collections.emptyList();

    protected boolean childrenDirty;

    @Override
    public Iterable<Component> getChildren() {
        updateChildren();
        return getItems().stream().map(children::get).collect(toList());
    }

    public Iterable<Pair<T, Component>> getItemsAndChildren() {
        updateChildren();
        return getItems().stream().map(i -> Pair.of(i, children.get(i)))
                .collect(toList());
    }

    public Optional<String> getTestName(T item) {
        if (testNameExtractor == null)
            return Optional.empty();
        return Optional.of(testNameExtractor.apply(item));
    }

    /**
     * update the child components if {@link #shouldRecalculateChildren()}
     * returns true
     */
    protected void updateChildren() {
        if (childrenDirty) {
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
            childrenDirty = false;
        }
    }

    /**
     * Clears the currently calculated child for an item
     */
    protected void clearChild(T item) {
        if (children.containsKey(item))
            childrenDirty = true;
        children.remove(item);
    }

    @Override
    public void childRemoved(Component child) {
        throw new UnsupportedOperationException();
    }

    public List<T> getItems() {
        return items;
    }

    public TSelf bindItems(Supplier<List<T>> itemsSupplier) {
        return bindTestNameProperty(c -> c.setItems(itemsSupplier.get()));
    }

    public TSelf setItems(List<T> items) {
        childrenDirty = true;
        this.items = new ArrayList<>(items);
        return self();
    }

    public Function<T, Component> getChildComponentFactory() {
        return childComponentFactory;
    }

    public TSelf setChildComponentFactory(
            Function<T, Component> childComponentFactory) {
        this.childComponentFactory = childComponentFactory;
        return self();
    }

    public Function<T, String> getTestNameExtractor() {
        return testNameExtractor;
    }

    public TSelf setTestNameExtractor(Function<T, String> testNameExtractor) {
        this.testNameExtractor = testNameExtractor;
        return self();
    }

}
