package laf.dataFlow;

public interface Output<T> extends Port<T> {

	void connectTo(Input<? super T> input);

	void set(PortState state, T value);

	DataFlowNode getNode();
}
