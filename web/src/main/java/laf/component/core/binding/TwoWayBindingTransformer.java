package laf.component.core.binding;


/**
 * Two-way transformation between two representations of a value
 */
public abstract class TwoWayBindingTransformer<TSource, TTarget> extends
		BindingTransformer<TSource, TTarget> {

	/**
	 * Perform the inverse transformation
	 */
	public final TSource transformInv(TTarget target) {

		BindingExpressionExecutionLog info = BindingExpressionExecutionLogManager.getCurrentLog();
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
