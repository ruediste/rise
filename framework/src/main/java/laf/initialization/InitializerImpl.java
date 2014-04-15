package laf.initialization;

public abstract class InitializerImpl implements Initializer {

	final private Class<?> componentClass;

	public InitializerImpl(Class<?> componentClass) {
		super();
		this.componentClass = componentClass;
	}

	@Override
	public Class<?> getRepresentingClass() {
		return componentClass;
	}
}
