package com.github.ruediste.rise.component.tree;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.ruediste.rise.util.Pair;

public class ItemsChildrenRelation<T, TChild extends Component, TContainingComponent extends RelationsComponent<TContainingComponent>>
        extends ChildRelation<TContainingComponent> {

    private Function<T, TChild> childComponentFactory;

    private Map<T, TChild> children = Collections.emptyMap();

    private List<T> items = new ArrayList<>();

    protected boolean childrenDirty;

    public ItemsChildrenRelation(TContainingComponent containingComponent) {
        super(containingComponent);
    }

    public TChild getChild(T item) {
        updateChildren();
        return children.get(item);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Component> iterator() {
        return (Iterator<Component>) getChildren().iterator();
    }

    public Iterable<TChild> getChildren() {
        updateChildren();
        return getItems().stream().map(children::get).collect(toList());
    }

    public TContainingComponent add(T item) {
        childrenDirty = true;
        this.items.add(item);
        return containingComponent;
    }

    public TContainingComponent setItems(List<T> items) {
        childrenDirty = true;
        this.items = new ArrayList<>(items);
        return containingComponent;
    }

    public Iterable<Pair<T, TChild>> getItemsAndChildren() {
        updateChildren();
        return getItems().stream().map(i -> Pair.of(i, children.get(i))).collect(toList());
    }

    /**
     * update the child components if {@link #childrenDirty} is true
     */
    protected void updateChildren() {
        if (childrenDirty) {
            Map<T, TChild> newChildren = new HashMap<>();
            for (T item : getItems()) {
                TChild child = children.get(item);
                if (child == null) {
                    child = getChildComponentFactory().apply(item);
                    child.parentChanged(containingComponent);
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

    public Function<T, TChild> getChildComponentFactory() {
        return childComponentFactory;
    }

    public TContainingComponent setChildComponentFactory(Function<T, TChild> childComponentFactory) {
        this.childComponentFactory = childComponentFactory;
        return containingComponent;
    }

}
