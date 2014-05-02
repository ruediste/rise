package laf.dataFlow;

import java.util.Collection;
import java.util.List;

public interface MultiInput<T> extends Port<T> {
	List<Output<? extends T>> getConnectedOutputs();

	void connectTo(Output<? extends T> output);

	Collection<T> get(PortState state);
}
