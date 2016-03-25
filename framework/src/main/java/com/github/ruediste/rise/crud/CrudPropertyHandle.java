package com.github.ruediste.rise.crud;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.binding.BindingGroup;

public interface CrudPropertyHandle {

    BindingGroup<?> group();

    /**
     * Return the proxy of the {@link #group()}. Useful to set up bindings.
     */
    default Object proxy() {
        return group().proxy();
    }

    CrudPropertyInfo info();

    void setValue(Object value);

    Object getValue();

    /**
     * Root entity owning the property. Can be null if no such entity is
     * available.
     */
    Object rootEntity();

    /**
     * @param objectSupplier
     *            object bearing the property. Invoking the supplier must cause
     *            the proxy of the supplied group to be accessed to enable
     *            binding
     */
    public static CrudPropertyHandle create(CrudPropertyInfo propertyInfo, Supplier<Object> rootEntitySupplier,
            Supplier<Object> objectSupplier, BindingGroup<?> group) {
        CrudPropertyHandle handle = new CrudPropertyHandle() {

            @Override
            public String toString() {
                return info().toString();
            }

            @Override
            public Object rootEntity() {
                return rootEntitySupplier.get();
            }

            @Override
            public CrudPropertyInfo info() {
                return propertyInfo;
            }

            @Override
            public BindingGroup<?> group() {
                return group;
            }

            @Override
            public void setValue(Object value) {
                propertyInfo.getProperty().setValue(objectSupplier.get(), value);

            }

            @Override
            public Object getValue() {
                return propertyInfo.getProperty().getValue(objectSupplier.get());
            }
        };
        return handle;
    }

}
