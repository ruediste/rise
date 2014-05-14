package laf.component;

import java.util.ArrayList;

import laf.attachedProperties.AttachedPropertyBearerBase;

import com.google.common.collect.Iterables;

public class ComponentBase extends AttachedPropertyBearerBase implements
Component {

	private Component parent;
	ArrayList<ChildRelation> childRelations = new ArrayList<>();

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

}
