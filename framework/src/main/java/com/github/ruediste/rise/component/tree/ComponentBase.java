package com.github.ruediste.rise.component.tree;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.binding.BindingUtil;

public abstract class ComponentBase<TSelf extends ComponentBase<TSelf>>
        extends AttachedPropertyBearerBase
        implements TestNamedComponent<TSelf>, Component {

    private Component parent;
    private String class_;
    private String testName;

    public ComponentBase() {
        super();
    }

    @SuppressWarnings("unchecked")
    public TSelf self() {
        return (TSelf) this;
    }

    @Override
    public Component getParent() {
        return parent;
    }

    @Override
    public void parentChanged(Component newParent) {
        parent = newParent;
    }

    public TSelf bind(Consumer<TSelf> property) {
        TSelf self = self();
        BindingUtil.bind(self, property);
        return self;
    }

    public TSelf bindOneWay(Consumer<TSelf> property) {
        TSelf self = self();
        BindingUtil.bindOneWay(self, property);
        return self;
    }

    /**
     * Establish an explicit binding
     */
    public <T> TSelf bind(Supplier<T> bindingGroupAccessor,
            BiConsumer<TSelf, T> pullUp, BiConsumer<TSelf, T> pushDown) {

        Binding<T> binding = new Binding<>();
        binding.setComponent(this);
        binding.setPullUp(d -> pullUp.accept(self(), d));
        binding.setPushDown(d -> pushDown.accept(self(), d));

        BindingUtil.bind(bindingGroupAccessor, binding);
        return self();
    }

    /**
     * Set the CSS-class to for to this component. It will generally be added to
     * the outermost HTML-element rendered for this component by the template.
     */
    public TSelf CLASS(String class_) {
        this.class_ = class_;
        return self();
    }

    public String CLASS() {
        return class_;
    }

    /**
     * Bind a property of this component. In addition, the {@link #TEST_NAME()}
     * is set to the name of the model property.
     * 
     * @see #bind(Consumer)
     */
    public TSelf bindTestNameProperty(Consumer<TSelf> binder) {
        TSelf self = self();
        TEST_NAME(BindingUtil.bind(self, binder).getB().modelPath
                .getAccessedProperty().getName());
        return self;
    }

    /**
     * Set the "data-test-name" attribute to for this component. It will
     * generally be added to the outermost HTML-element rendered for this
     * component by the template.
     */
    @Override
    public TSelf TEST_NAME(String testName) {
        this.testName = testName;
        return self();
    }

    @Override
    public String TEST_NAME() {
        return testName;
    }

    public TSelf apply(Consumer<TSelf> consumer) {
        TSelf self = self();
        consumer.accept(self);
        return self;
    }
}