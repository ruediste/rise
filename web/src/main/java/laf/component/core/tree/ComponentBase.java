package laf.component.core.tree;

import java.util.ArrayList;
import java.util.function.Consumer;

import laf.component.core.binding.BindingUtil;
import laf.core.base.attachedProperties.AttachedPropertyBearer;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

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

	public TSelf bind(Runnable bindingAccessor, Consumer<TSelf> pullUp,
			Consumer<TSelf> pushDown) {

		// BindingUtil.bind(self(), bindingAccessor, pullUp, pushDown);
		return self();
	}

}
