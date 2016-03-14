package com.github.ruediste.rise.component.components;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.component.tree.SingleChildRelation;

/**
 * Component allowing to switch on click from a display view to an edit view and
 * back to the display view when the focus on the edit view is lost.
 */
@DefaultTemplate(CCLickEditTemplate.class)
public class CClickEdit<T> extends RelationsComponent<CClickEdit<T>> implements LabeledComponentTrait<CClickEdit<T>> {

    private SingleChildRelation<Component, CClickEdit<T>> child = new SingleChildRelation<>(this);
    private T value;

    private BiConsumer<T, EditComponentConsumer<T>> editComponentFactory;
    private Function<T, Component> viewComponentFactory;

    private Supplier<T> valueExtractor;
    private boolean isEdit;

    private Component focusComponent;

    private boolean focusEditComponentOnReload;
    private LabeledComponentStatus labeledComponentStatus = new LabeledComponentStatus();

    CClickEdit() {
    }

    public interface EditComponentConsumer<T> {
        /**
         * Set the edit component
         * 
         * @param component
         *            the component to show as edit component
         * @param valueExtractor
         *            extactor to retrieve the edited value when the focus on
         *            the edit component is lost
         * @param focusComponent
         *            component to focus on
         */
        void setEditComponent(Component component, Supplier<T> valueExtractor, Component focusComponent);
    }

    private static class EditComponentConsumerImpl<T> implements EditComponentConsumer<T> {

        Component component;
        Supplier<T> valueExtractor;
        Component focusComponent;
        boolean setEditComponentCalled;

        @Override
        public void setEditComponent(Component component, Supplier<T> valueExtractor, Component focusComponent) {
            setEditComponentCalled = true;
            this.component = component;
            this.valueExtractor = valueExtractor;
            this.focusComponent = focusComponent;
        }

    }

    public CClickEdit(Function<T, Component> viewComponentFactory,
            BiConsumer<T, EditComponentConsumer<T>> editComponentFactory) {
        this.viewComponentFactory = viewComponentFactory;
        this.editComponentFactory = editComponentFactory;
    }

    public T getValue() {
        return value;
    }

    public CClickEdit<T> setValue(T value) {
        this.value = value;
        updateChild();
        return this;
    }

    /**
     * Bind the value to a property, set the label if the property is labeled
     * and set the {@link #TEST_NAME()} to the name of the property
     */
    public CClickEdit<T> bindValue(Supplier<T> supplier) {
        return bindLabelProperty(x -> x.setValue(supplier.get()));
    }

    public BiConsumer<T, EditComponentConsumer<T>> getEditComponentFactory() {
        return editComponentFactory;
    }

    public Function<T, Component> getViewComponentFactory() {
        return viewComponentFactory;
    }

    public void switchToView() {
        if (isEdit) {
            isEdit = false;
            value = valueExtractor.get();
            valueExtractor = null;
            focusComponent = null;
            focusEditComponentOnReload = false;
            updateChild();
        }
    }

    public void switchToEdit() {
        isEdit = true;
        focusEditComponentOnReload = true;
        updateChild();
    }

    private void updateChild() {
        if (isEdit) {
            EditComponentConsumerImpl<T> consumer = new EditComponentConsumerImpl<>();
            editComponentFactory.accept(value, consumer);
            if (!consumer.setEditComponentCalled)
                throw new RuntimeException(
                        "EditComponentFactory has the set the component on the supplied EditComponentConsumer");
            child.setChild(consumer.component);
            valueExtractor = consumer.valueExtractor;
            focusComponent = consumer.focusComponent;
        } else {
            child.setChild(viewComponentFactory.apply(value));
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public Component getFocusComponent() {
        return focusComponent;
    }

    public boolean isFocusEditComponentOnReload() {
        return focusEditComponentOnReload;
    }

    public boolean isFocusEditComponentOnReloadAndClear() {
        boolean tmp = focusEditComponentOnReload;
        focusEditComponentOnReload = false;
        return tmp;
    }

    public void setFocusEditComponentOnReload(boolean focusEditComponentOnReload) {
        this.focusEditComponentOnReload = focusEditComponentOnReload;
    }

    @Override
    public LabeledComponentStatus internal_getLabeledComponentStatus() {
        return labeledComponentStatus;
    }
}
