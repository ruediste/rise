package com.github.ruediste.rise.component.binding;

/**
 * Transform a value from one representation to another
 */
public abstract class BindingTransformer<TSource, TTarget> {

    public final TTarget transform(TSource source) {
        return transformImpl(source);
    }

    protected abstract TTarget transformImpl(TSource source);

}
