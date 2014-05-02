package laf.dataFlow;

import java.util.HashMap;

public class PortState {

	final HashMap<Port<?>, Object> state = new HashMap<>();

	@SuppressWarnings("unchecked")
	<T> T get(Output<T> output) {
		return (T) state.get(output);
	}

	<T> void set(Output<T> output, T value) {
		state.put(output, value);
	}

	@Override
	public String toString() {
		return state.toString();
	}
}
