package laf.component.core;

import java.io.IOException;
import java.util.ArrayList;

import laf.attachedProperties.AttachedPropertyBearerBase;
import laf.component.html.ApplyValuesUtil;
import laf.component.html.RenderUtil;
import laf.component.html.template.RaiseEventsUtil;

import org.rendersnake.HtmlCanvas;

import com.google.common.collect.Iterables;

public class ComponentBase<TSelf> extends AttachedPropertyBearerBase implements
Component {

	private Component parent;
	ArrayList<ChildRelation<?>> childRelations = new ArrayList<>();
	private Integer id;

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
		for (ChildRelation<?> relation : childRelations) {
			relation.childRemoved(child);
		}
	}

	public void addChildRelation(ChildRelation<?> childRelation) {
		childRelations.add(childRelation);
	}

	@Override
	public void render(HtmlCanvas html, RenderUtil util) throws IOException {
		for (Component child : getChildren()) {
			child.render(html, util.forChild(child));
		}
	}

	@Override
	public void initialize() {

	}

	@Override
	public void applyValues(ApplyValuesUtil util) {
		// TODO Auto-generated method stub

	}

	@Override
	public void raiseEvents(RaiseEventsUtil util) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getComponentId() {
		return id;
	}

	@Override
	public void setComponentId(Integer id) {
		this.id = id;
	}
}
