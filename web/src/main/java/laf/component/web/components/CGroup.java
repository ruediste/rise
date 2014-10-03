package laf.component.web.components;


public class CGroup extends MultiChildrenComponent<CGroup> {

	public CGroup render(Renderer renderer) {
		children.add(new CRender(renderer));
		return self();
	}
}
