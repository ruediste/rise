package laf.component;

import java.io.IOException;
import java.util.ArrayList;

import laf.attachedProperties.AttachedPropertyBearerBase;

import org.rendersnake.HtmlCanvas;

import com.google.common.collect.Iterables;

public class ComponentBase<TSelf> extends AttachedPropertyBearerBase implements
Component {

	private Component parent;
	ArrayList<ChildRelation> childRelations = new ArrayList<>();

	protected ComponentUtil util = ComponentUtil.getInstance();

	@Override
	public Iterable<Component> getChildren() {
		return Iterables.concat(childRelations);
	}

	@Override
	public Component getParent() {
		return parent;
	}

	@Override
	public void parentChanged(Component newParent) {
		parent = newParent;
	}

	@Override
	public void childRemoved(Component child) {
		for (ChildRelation relation : childRelations) {
			relation.childRemoved(child);
		}
	}

	public void addChildRelation(ChildRelation childRelation) {
		childRelations.add(childRelation);
	}

	@Override
	public void render(HtmlCanvas html) throws IOException {
		for (Component child : getChildren()) {
			child.render(html);
		}
	}

	@Override
	public void initialize() {

	}
}
