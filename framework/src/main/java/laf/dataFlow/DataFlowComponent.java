package laf.dataFlow;

import java.util.*;

public class DataFlowComponent extends DataFlowNode {

	DataFlowComponent() {
		instantiatePorts(getClass());
	}

	final ArrayList<DataFlowNode> inputs = new ArrayList<>();
	final ArrayList<DataFlowNode> outputs = new ArrayList<>();
	public final LinkedList<DataFlowNode> nodes = new LinkedList<>();

	@Override
	public void execute(PortState portState) {

		for (DataFlowNode node : inputs) {
			node.execute(portState);
		}
		for (DataFlowNode node : nodes) {
			node.execute(portState);
		}
		for (DataFlowNode node : outputs) {
			node.execute(portState);
		}
	}

	/**
	 * Validate this component. Add errors to the given list.
	 */
	public void validate(List<String> errors) {
		HashSet<DataFlowComponent> visitedComponents = new HashSet<>();
		validate(visitedComponents, errors);
	}

	void validate(Set<DataFlowComponent> visitedComponents, List<String> errors) {
		if (!visitedComponents.add(this)) {
			errors.add("Detected loob at component " + this);
			return;
		}

		HashSet<DataFlowNode> allNodes = new HashSet<>();
		allNodes.addAll(inputs);
		allNodes.addAll(nodes);
		allNodes.addAll(outputs);
		for (DataFlowNode node : nodes) {
			// check sub components
			if (node instanceof DataFlowComponent) {
				((DataFlowComponent) node).validate(visitedComponents, errors);
			}

			// check if all inputs are connected
			for (Port<?> port : node.getPorts()) {
				if (port instanceof Input) {
					Input<?> input = (Input<?>) port;
					if (input.getConnectedOutput() == null) {
						errors.add("Input " + input + " of node " + node
								+ " is not connected");
					} else {
						if (!allNodes.contains(input.getConnectedOutput()
								.getNode())) {
							errors.add("Connection between output "
									+ input.getConnectedOutput()
									+ " and input "
									+ input
									+ " spans a component border. Involved component: "
									+ this);
						}
					}
				}
			}
		}
	}
}
