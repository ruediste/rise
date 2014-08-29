package laf.mvc.core;

/**
 * Base Class for views of the MVC framework
 */
public class MvcView<TData> {

	private TData data;

	public final void initialize(TData data) {
		this.data = data;
	}

	public TData getData() {
		return data;
	}
}