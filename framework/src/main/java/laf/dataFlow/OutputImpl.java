package laf.dataFlow;

class OutputImpl<T> implements Output<T> {

	final private String name;
	final private DataFlowNode node;

	OutputImpl(DataFlowNode node, String name) {
		this.node = node;
		this.name = name;
		node.addPort(this);
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
	public String toString() {
		return getNode() + "." + getName();
	}

	public String getName() {
		return name;
	}

	public DataFlowNode getNode() {
		return node;
	}
}
