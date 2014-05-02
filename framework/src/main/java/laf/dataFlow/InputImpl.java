package laf.dataFlow;

class InputImpl<T> implements Input<T> {

	private Output<? extends T> connectedOutput;
	private final String name;
	private DataFlowNode node;

	InputImpl(DataFlowNode node, String name) {
		this.node = node;
		this.name = name;
		node.addPort(this);
	}

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
		return state.get(connectedOutput);
	}

	@Override
	public String toString() {
		return node + "." + getName();
	}

	@Override
	public String getName() {
		return name;
	}
}
