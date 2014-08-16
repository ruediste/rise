package laf.mvc.html;

/**
 * Base Class for views of the MVC framework
 */
public class View<TData> {

	private TData data;

	public final void initialize(TData data) {
		this.data = data;
	}

	public TData getData() {
		return data;
	}
}