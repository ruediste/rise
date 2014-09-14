package laf.component.core.tree;

import java.util.ArrayList;
import java.util.function.Consumer;

import laf.component.core.binding.BindingUtil;
import laf.core.base.attachedProperties.AttachedPropertyBearer;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

import com.google.common.collect.Iterables;

/*
 @startuml doc-files/test.png
 Class01 "1" *-- "many" Class02 : contains
 Class03 o-- Class04 : agregation
 Class05 --> "1" Class06
 @enduml

 */

/*
 @startuml doc-files/childRelation.png
 class ComponentBase {
 Iterable<Component> getComponents(): \n get components from\n child relations
 }
 ComponentBase ->  ChildRelation
 class ChildRelation {
 }

 class "Iterable<Component>" as iterable{
 }

 iterable <|-- ChildRelation

 class "SingleChildRelation<TChild>" as SingleChildRelation {
 setChild(TChild child)
 TChild getChild()
 }
 ChildRelation <|-- SingleChildRelation

 class "MultiChildrenRelation<TChild>" as MultiChildrenRelation {
 addChild(TChild child)
 removeChild(TChild child)
 Collection<TChild> getChildren()
 }
 ChildRelation <|-- MultiChildrenRelation

 @enduml
 */

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
