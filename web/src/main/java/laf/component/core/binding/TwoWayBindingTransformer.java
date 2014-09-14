package laf.component.core.binding;

import laf.component.core.binding.ProxyManger.BindingInformation;

/**
 * Two-way transformation between two representations of a value
 */
public abstract class TwoWayBindingTransformer<TSource, TTarget> extends
		BindingTransformer<TSource, TTarget> {

	/**
	 * Perform the inverse transformation
	 */
	public final TSource transformInv(TTarget target) {

		BindingInformation info = ProxyManger.getCurrentInformation();
		if (info == null) {
			return transformInvImpl(target);
		} else {
			info.transformer = this;
			info.transformInv = true;
			return null;
		}
	}

	protected abstract TSource transformInvImpl(TTarget target);

}
