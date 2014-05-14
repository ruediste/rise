package laf.component;

public class PageBase<T> {

	protected T controller;

	public T getController() {
		return controller;
	}

	public void setController(T controller) {
		this.controller = controller;
	}

}
