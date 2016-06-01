package com.github.ruediste.rise.component.tree;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;

public abstract class ComponentBase<TSelf extends ComponentBase<TSelf>> extends AttachedPropertyBearerBase
        implements TestNamedComponent<TSelf>, Component {

    private String class_;
    private String testName;
    private Optional<Boolean> disabled = Optional.empty();

    public ComponentBase() {
        super();
    }

    @SuppressWarnings("unchecked")
    public TSelf self() {
        return (TSelf) this;
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
    public TSelf TEST_NAME(@Capture Supplier<?> supplier) {
        BindingUtil.tryExtractBindingInfo(supplier).ifPresent(info -> TEST_NAME(info.modelProperty.getName()));
        return self();
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

    /**
     * Return true if this component is disabled, either directly or through
     * inheritance
     */
    @Deprecated
    public boolean isDisabled() {
        if (disabled.isPresent()) {
            return disabled.get();
        }
        return false;
    }

    /**
     * Return the disabled flag of this component without taking inheritance
     * into account
     */
    public Optional<Boolean> getDisabled() {
        return disabled;
    }

    public TSelf setDisabled(Optional<Boolean> disabled) {
        this.disabled = disabled;
        return self();
    }

    public TSelf setDisabled(Boolean disabled) {
        if (disabled)
            this.disabled = Optional.of(true);
        else
            this.disabled = Optional.empty();
        return self();
    }

    public TSelf disable() {
        this.disabled = Optional.of(true);
        return self();
    }

}