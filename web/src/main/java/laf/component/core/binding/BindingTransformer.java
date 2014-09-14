package laf.component.core.binding;

import laf.component.core.binding.ProxyManger.BindingInformation;

/**
 * Transform a value from one representation to another
 */
public abstract class BindingTransformer<TSource, TTarget> {

	/**
	 * Transform the view value to the model value
	 */
	public final TTarget transform(TSource source) {
		BindingInformation info = ProxyManger.getCurrentInformation();
		if (info == null) {
			return transformImpl(source);
		} else {
			info.transformer = this;
			return null;
		}
	}

	protected abstract TTarget transformImpl(TSource source);

}
