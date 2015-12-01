package com.github.ruediste.rise.component.components;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.ViolationStatus;
import com.github.ruediste.rise.component.ViolationStatusBearer;
import com.google.common.base.Preconditions;

/**
 * Component showing a list of items and allowing a single one to be selected.
 */
public class CSingleSelection<T, TSelf extends CSingleSelection<T, TSelf>>
        extends CItems<T, TSelf>
        implements LabeledComponentTrait<TSelf>, ViolationStatusBearer {

    private Optional<T> selectedItem = Optional.empty();

    private LabeledComponentStatus labeledComponentStatus = new LabeledComponentStatus();
    private ViolationStatus violationStatus = new ViolationStatus();

    public boolean isItemSelected(T item) {
        return selectedItem.equals(Optional.of(item));
    }

    public Optional<T> getSelectedItem() {
        return selectedItem;
    }

    public TSelf setSelectedItem(Optional<T> selectedItem) {
        Preconditions.checkNotNull(selectedItem);
        this.selectedItem = selectedItem;
        return self();
    }

    public TSelf bindSelectedItem(Supplier<Optional<T>> accessor) {
        return bindLabelProperty(x -> x.setSelectedItem(accessor.get()));
    }

    @Override
    public LabeledComponentStatus internal_getLabeledComponentStatus() {
        return labeledComponentStatus;
    }

    @Override
    public ViolationStatus getViolationStatus() {
        return violationStatus;
    }
}
