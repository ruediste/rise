package laf.dataFlow;

public class SampleOperationA extends DataFlowOperation {

	Input<Integer> i1;
	Output<Double> o1;

	@Override
	public void execute(PortState portState) {
		Integer i1Value = i1.get(portState);
		o1.set(portState, i1 == null ? null : i1Value.doubleValue());
	}
}
