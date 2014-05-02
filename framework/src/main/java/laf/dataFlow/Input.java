package laf.dataFlow;

public interface Input<T> extends Port<T> {
	Output<? extends T> getConnectedOutput();

	void connectTo(Output<? extends T> output);

	T get(PortState state);

}
