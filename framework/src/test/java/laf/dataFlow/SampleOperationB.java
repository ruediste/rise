package laf.dataFlow;

public class SampleOperationB extends DataFlowOperation {

	Input<Double> i1;

	Output<String> o1;
	Output<Integer> o2;

	@Override
	public void execute(PortState portState) {
		Double i1Value = i1.get(portState);
		o1.set(portState, i1Value == null ? null : i1Value.toString());
		o2.set(portState, i1Value == null ? null : i1Value.intValue());
	}
}
