package laf.dataFlow;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class DataFlowTest {

	ArrayList<String> errors;

	@Before
	public void initialize() {
		errors = new ArrayList<>();
	}

	@Test
	public void componentConstruction() {
		SampleComponent component = new SampleComponent();
		assertEquals(1, component.inputs.size());
		assertEquals(2, component.outputs.size());
		assertEquals(3, component.getPorts().size());

		assertNull(component.in.getConnectedOutput());
	}

	@Test
	public void operationConstruction() {
		SampleOperationA a = new SampleOperationA();
		assertEquals(2, a.getPorts().size());
	}

	@Test
	public void execute() {
		SampleComponent component = new SampleComponent();

		SampleOperationA a = new SampleOperationA();
		component.nodes.add(a);
		a.i1.connectTo(component.in.output);
		component.in.output.connectTo(a.i1);

		SampleOperationB b = new SampleOperationB();
		component.nodes.add(b);
		a.o1.connectTo(b.i1);

		b.o1.connectTo(component.o2.input);
		b.o2.connectTo(component.o1.input);

		PortState state = new PortState();
		component.in.output.set(state, 3);
		component.execute(state);
		assertEquals(Integer.valueOf(3), component.o1.input.get(state));
		assertEquals("3.0", component.o2.input.get(state));
	}

	@Test
	public void isValidEmpty() {
		SampleComponent component = new SampleComponent();

		component.validate(errors);
		assertTrue(errors.isEmpty());
	}

	@Test
	public void isValidUnconnectedInput() {
		SampleComponent component = new SampleComponent();
		SampleOperationA a = new SampleOperationA();
		component.nodes.add(a);
		component.validate(errors);
		assertFalse(errors.isEmpty());
	}

	@Test
	public void isValidCheckUnconnectedComponnetInput() {
		SampleComponent c1 = new SampleComponent();
		SampleComponent c2 = new SampleComponent();
		c1.nodes.add(c2);

		c1.validate(errors);
		assertFalse(errors.isEmpty());
	}

	@Test
	public void isValidCheckSubComponents() {
		SampleComponent c1 = new SampleComponent();
		SampleComponent c2 = new SampleComponent();
		c1.nodes.add(c2);
		c2.in.connectTo(c1.in.output);
		SampleOperationA a = new SampleOperationA();
		c2.nodes.add(a);

		c1.validate(errors);
		assertFalse(errors.isEmpty());
	}

	@Test(timeout = 100)
	public void isValidLoop() {
		SampleComponent c1 = new SampleComponent();
		SampleComponent c2 = new SampleComponent();
		c1.nodes.add(c2);
		c2.nodes.add(c1);
		c1.validate(errors);
	}

	@Test
	public void isValidInputConnectedOverComponentBorder() {
		SampleComponent c1 = new SampleComponent();
		SampleComponent c2 = new SampleComponent();

		SampleOperationA a = new SampleOperationA();
		c1.nodes.add(a);

		a.i1.connectTo(c2.in.output);

		c1.validate(errors);
		assertFalse(errors.isEmpty());

	}
}
