package laf.dataFlow;

public class ComponentOutput<T> extends DataFlowNode implements Output<T> {
	private final String name;

	/**
	 * The input presented by the {@link DataFlowComponent} to the outside
	 * world.
	 */
	public Input<T> input;

	private final DataFlowComponent component;

	ComponentOutput(DataFlowComponent component, String name) {
		this.component = component;
		component.outputs.add(this);
		this.name = name;
		component.addPort(this);
		instantiatePorts(getClass());
	}

	@Override
	public void connectTo(Input<? super T> input) {
		input.connectTo(this);
	}

	@Override
	public void set(PortState state, T value) {
		state.set(this, value);
	}

	@Override
	public void execute(PortState portState) {
		set(portState, input.get(portState));
	}

	@Override
	public String toString() {
		return component + "." + getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public DataFlowNode getNode() {
		return component;
	}
}
