package com.github.ruediste.rise.component.components;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

/**
 * Component showing a list of items and allowing a single one to be selected.
 */
public class CSingleSelection<T, TSelf extends CSingleSelection<T, TSelf>> extends CItems<T, TSelf>
        implements LabeledComponentTrait<TSelf> {

    private Optional<T> selectedItem = Optional.empty();

    private LabeledComponentStatus labeledComponentStatus = new LabeledComponentStatus();

    private Consumer<Optional<T>> selectionHandler;

    public boolean isItemSelected(T item) {
        return selectedItem.equals(Optional.of(item));
    }

    public Optional<T> getSelectedItem() {
        return selectedItem;
    }

    public TSelf setSelectedItem(Optional<T> selectedItem) {
        Preconditions.checkNotNull(selectedItem);
        boolean invokeHandler = selectionHandler != null && !Objects.equals(this.selectedItem, selectedItem);
        this.selectedItem = selectedItem;
        if (invokeHandler)
            selectionHandler.accept(selectedItem);
        return self();
    }

    public TSelf bindSelectedItem(Supplier<Optional<T>> accessor) {
        return bindLabelProperty(x -> x.setSelectedItem(accessor.get()));
    }

    @Override
    public LabeledComponentStatus internal_getLabeledComponentStatus() {
        return labeledComponentStatus;
    }

    public TSelf setSelectionHandler(Consumer<Optional<T>> selectionHandler) {
        this.selectionHandler = selectionHandler;
        return self();
    }

    public Consumer<Optional<T>> getSelectionHandler() {
        return selectionHandler;
    }

}
