package laf.component.core.binding;

/**
 * Transform a value between a representation in the view and another
 * representation in the model.
 */
public abstract class BindingTransformer<TView, TModel> {

	/**
	 * Transform the model value to the view value
	 */
	public abstract TView transformPullUp(TModel model);

	/**
	 * Transform the view value to the model value
	 */
	public abstract TModel transformPushDown(TView view);

	/**
	 * Transform the model value to the view model. If used in a bidirectional
	 * binding, the use of the binding transformer is recorded and registered
	 * with the binding
	 */
	public TView transform(TModel model) {
		return transformPullUp(model);
	}
}
