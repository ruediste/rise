package laf.dataFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic data flow library.
 *
 * <p>
 * To implement the request handling pipeline, a data flow approach was chosen.
 * This allows total configuration of the pipeline while keeping the ability to
 * verify the dependencies between the individual operations.
 * </p>
 *
 * <p>
 * <img src="doc-files/Overview.png" />
 * </p>
 *
 * <p>
 * Invariants:
 * <ul>
 * <li>For all inputs connected to an outputs, the output type has to be
 * assignable to the input type</li>
 * <li></li>
 * <li></li>
 * </ul>
 * </p>
 */
public class DataFlowModule {

	public final List<DataFlowNode> nodes = new ArrayList<>();
}
