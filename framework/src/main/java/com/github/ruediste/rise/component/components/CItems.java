package com.github.ruediste.rise.component.components;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ItemsChildrenRelation;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.util.Pair;

/**
 * Base class for components displaying a set of items. Null items are not
 * allowed.
 */
public class CItems<T, TChild extends Component, TSelf extends CItems<T, TChild, TSelf>>
        extends RelationsComponent<TSelf> {

    private final ItemsChildrenRelation<T, TChild, TSelf> childRelation = new ItemsChildrenRelation<>(
            self());

    private Function<T, String> testNameExtractor;

    public Iterable<Pair<T, TChild>> getItemsAndChildren() {
        return childRelation().getItemsAndChildren();
    }

    public Optional<String> getTestName(T item) {
        if (testNameExtractor == null)
            return Optional.empty();
        return Optional.of(testNameExtractor.apply(item));
    }

    public List<T> getItems() {
        return childRelation().getItems();
    }

    public TSelf bindItems(Supplier<List<T>> itemsSupplier) {
        return bindTestNameProperty(c -> c.setItems(itemsSupplier.get()));
    }

    public TSelf setItems(List<T> items) {
        return this.childRelation.setItems(items);
    }

    public Function<T, TChild> getChildComponentFactory() {
        return childRelation().getChildComponentFactory();
    }

    public TSelf setChildComponentFactory(Function<T, TChild> childComponentFactory) {
        return childRelation().setChildComponentFactory(childComponentFactory);
    }

    public Function<T, String> getTestNameExtractor() {
        return testNameExtractor;
    }

    public TSelf setTestNameExtractor(Function<T, String> testNameExtractor) {
        this.testNameExtractor = testNameExtractor;
        return self();
    }

    public ItemsChildrenRelation<T, TChild, TSelf> childRelation() {
        return childRelation;
    }

}
