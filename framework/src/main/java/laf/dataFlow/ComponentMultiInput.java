package laf.dataFlow;

import java.util.*;

/**
 * A {@link ComponentMultiInput} represents an input of a component. It is a
 * {@link DataFlowNode} of the {@link DataFlowComponent} and has one
 * {@link #output}, to which other nodes of the component can connect.
 *
 * <p>
 * At the same time it is an input of the containing component to which other
 * nodes can connect.
 * </p>
 *
 */
public class ComponentMultiInput<T> extends DataFlowNode implements
MultiInput<T> {

	final private String name;

	private final DataFlowComponent component;

	ComponentMultiInput(DataFlowComponent component, String name) {
		this.component = component;
		this.name = name;
		component.inputs.add(this);
		component.addPort(this);
		instantiatePorts(getClass());
	}

	public Output<Collection<T>> output;

	private ArrayList<Output<? extends T>> connectedOutputs = new ArrayList<>();

	@Override
	public void connectTo(Output<? extends T> output) {
		connectedOutputs.add(output);
	}

	@Override
	public List<Output<? extends T>> getConnectedOutputs() {
		return connectedOutputs;
	}

	@Override
	public Collection<T> get(PortState state) {
		ArrayList<T> result = new ArrayList<>();
		for (Output<? extends T> o : connectedOutputs) {
			result.add(state.get(o));
		}
		return result;
	}

	@Override
	public void execute(PortState portState) {
		output.set(portState, get(portState));
	}

	@Override
	public String toString() {
		return component + "." + getName();
	}

	@Override
	public String getName() {
		return name;
	}
}
