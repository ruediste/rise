package com.github.ruediste.laf.component.tree;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.laf.component.core.binding.Binding;
import com.github.ruediste.laf.component.core.binding.BindingUtil;
import com.google.common.collect.Iterables;

/**
 * Base class for the {@link Component} interface. Implements the parent-child
 * relation using {@link ChildRelation}s.
 *
 * <p>
 * <img src="doc-files/childRelation.png" />
 * </p>
 *
 * @param <TSelf>
 *            type of this component
 */
public class ComponentBase<TSelf extends AttachedPropertyBearer> extends
		AttachedPropertyBearerBase implements Component {

	private Component parent;
	ArrayList<ChildRelation<?>> childRelations = new ArrayList<>();
	private String tag;

	@SuppressWarnings("unchecked")
	protected TSelf self() {
		return (TSelf) this;
	}

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
	public void initialize() {

	}

	public TSelf bind(Consumer<TSelf> property) {
		BindingUtil.bind(self(), property);
		return self();
	}

	public TSelf bindOneWay(Consumer<TSelf> property) {
		BindingUtil.bindOneWay(self(), property);
		return self();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TSelf bind(Supplier<?> bindingAccessor, Consumer<TSelf> pullUp,
			Consumer<TSelf> pushDown) {

		Binding<?> binding = new Binding<>();
		binding.setComponent(this);
		binding.setPullUp(d -> pullUp.accept(self()));
		binding.setPushDown(d -> pushDown.accept(self()));

		BindingUtil.bind(bindingAccessor, (Binding) binding);
		return self();
	}

	public TSelf tag(String tag) {
		this.tag = tag;
		return self();
	}

	public String tag() {
		return tag;
	}
}
