package laf.dataFlow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class DataFlowNode {

	private final Set<Port<?>> ports = new HashSet<>();

	DataFlowNode() {
	}

	void instantiatePorts(Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		instantiatePorts(clazz.getSuperclass());

		for (Field field : clazz.getDeclaredFields()) {
			// skip private fields
			if (Modifier.isPrivate(field.getModifiers())) {
				continue;
			}

			try {
				if (Input.class.equals(field.getType())) {
					field.setAccessible(true);
					field.set(this, new InputImpl<>(this, field.getName()));
				}

				if (Output.class.equals(field.getType())) {
					field.setAccessible(true);
					field.set(this,
							new OutputImpl<Object>(this, field.getName()));
				}

				if (this instanceof DataFlowComponent) {
					if (ComponentInput.class.equals(field.getType())) {
						field.setAccessible(true);
						field.set(this, new ComponentInput<Object>(
								(DataFlowComponent) this, field.getName()));
					}
					if (ComponentMultiInput.class.equals(field.getType())) {
						field.setAccessible(true);
						field.set(this, new ComponentMultiInput<>(
								(DataFlowComponent) this, field.getName()));
					}

					if (ComponentOutput.class.equals(field.getType())) {
						field.setAccessible(true);
						field.set(this, new ComponentOutput<Object>(
								(DataFlowComponent) this, field.getName()));
					}

				}

			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	final public Set<Port<?>> getPorts() {
		return Collections.unmodifiableSet(ports);
	}

	void addPort(Port<?> port) {
		ports.add(port);
	}

	public abstract void execute(PortState portState);

}
