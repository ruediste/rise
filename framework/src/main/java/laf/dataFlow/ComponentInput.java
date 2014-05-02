package laf.dataFlow;

/**
 * A {@link ComponentInput} represents an input of a component. It is a
 * {@link DataFlowNode} of the {@link DataFlowComponent} and has one
 * {@link #output}, to which other nodes of the component can connect.
 *
 * <p>
 * At the same time it is an input of the containing component to which other
 * nodes can connect.
 * </p>
 *
 */
public class ComponentInput<T> extends DataFlowNode implements Input<T> {

	private String name;

	private final DataFlowComponent component;

	ComponentInput(DataFlowComponent component, String name) {
		this.component = component;
		component.inputs.add(this);
		this.name = name;
		component.addPort(this);
		instantiatePorts(getClass());
	}

	public Output<T> output;

	private Output<? extends T> connectedOutput;

	@Override
	public Output<? extends T> getConnectedOutput() {
		return connectedOutput;
	}

	@Override
	public void connectTo(Output<? extends T> output) {
		connectedOutput = output;
	}

	@Override
	public T get(PortState state) {
		return state.get(output);
	}

	@Override
	public void execute(PortState portState) {
		output.set(portState, get(portState));
	}

	@Override
	public String toString() {
		return component + "." + name;
	}

	@Override
	public String getName() {
		return name;
	}
}
