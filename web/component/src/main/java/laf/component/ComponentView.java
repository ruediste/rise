package laf.component;

public abstract class ComponentView<TController> extends
		ComponentBase<ComponentView<TController>> {

	protected final MultiChildrenRelation<Component> children = new MultiChildrenRelation<>(
			this);

	protected TController controller;

	public TController getController() {
		return controller;
	}

	public void setController(TController controller) {
		this.controller = controller;
	}

	public void createComponents() {
	}
}
