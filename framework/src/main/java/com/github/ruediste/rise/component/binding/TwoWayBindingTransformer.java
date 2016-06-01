package com.github.ruediste.rise.component.binding;

/**
 * Two-way transformation between two representations of a value
 */
public abstract class TwoWayBindingTransformer<TSource, TTarget> extends BindingTransformer<TSource, TTarget> {

    /**
     * Perform the inverse transformation
     */
    public final TSource transformInv(TTarget target) {
        return transformInvImpl(target);
    }

    protected abstract TSource transformInvImpl(TTarget target);

}
